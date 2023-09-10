package tech.fika.macaron.core.components

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
class DefaultStore<I : Intent, A : Action, S : State>(
    initialState: S,
    private val processor: Processor<I, A, S>,
    private val reducer: Reducer<A, S>,
    private val middlewares: List<Middleware<I, A, S>> = emptyList(),
    coroutineContext: CoroutineContext = Dispatchers.Main.immediate,
) : Store<I, A, S> {
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())
    private val intents = MutableSharedFlow<I>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)
    private val _state = MutableStateFlow(initialState)
    private val _events = MutableStateFlow(emptyList<Action.Event>())
    override val state = _state
    override val currentState: S get() = _state.value
    override val event = _events.map { it.firstOrNull() as A? }

    init {
        scope.launch {
            intents
                .applyIntentMiddlewares(middlewares)
                .flatMapMerge { intent -> processor.process(intent, currentState) }
                .applyActionMiddlewares(middlewares)
                .mapNotNull { action ->
                    if (action is Action.Event) action.effect()
                    reducer.reduce(action, currentState)
                }
                .applyStateMiddlewares(middlewares)
                .collect { state -> _state.value = state }
        }
    }

    override fun dispatch(intent: I) {
        scope.launch { intents.emit(intent) }
    }

    override fun process(event: A) {
        scope.launch { _events.emit(_events.value.filterNot { it == event }) }
    }

    private suspend fun Action.Event.effect() {
        _events.emit(_events.value + this)
    }

    override fun dispose() {
        scope.cancel()
    }

    override fun collect(
        onState: (S) -> Unit,
        onEvent: (A?) -> Unit,
    ): Job = scope.launch {
        launch { state.collect { onState(it) } }
        launch { event.collect { onEvent(it) } }
    }

    /**
     * Helper functions to apply [Middleware] to their intended stream
     */
    private fun Flow<I>.applyIntentMiddlewares(
        middlewares: List<Middleware<I, A, S>>,
    ): Flow<I> = middlewares.fold(this) { intents, middleware ->
        middleware.modifyIntents(intents, ::currentState)
    }

    private fun Flow<A>.applyActionMiddlewares(
        middlewares: List<Middleware<I, A, S>>,
    ): Flow<A> = middlewares.fold(this) { actions, middleware ->
        middleware.modifyActions(actions, ::currentState)
    }

    private fun Flow<S>.applyStateMiddlewares(
        middlewares: List<Middleware<I, A, S>>,
    ): Flow<S> = middlewares.fold(this) { state, middleware ->
        middleware.modifyStates(state)
    }
}
