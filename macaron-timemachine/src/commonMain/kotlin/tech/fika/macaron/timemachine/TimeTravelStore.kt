package tech.fika.macaron.timemachine

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tech.fika.macaron.core.components.Interpreter
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.components.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Contract
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

interface TimeTravelStore<I : Intent, S : State, E : Event> : Store<I, S, E> {
    val entries: Flow<TimeMachineEntry>

    fun restoreState()

    fun process(id: Long)

    fun debug(id: Long)
}

@OptIn(FlowPreview::class)
class DefaultTimeTravelStore<I : Intent, A : Action, R : Result, S : State, E : Event>(
    initialState: S,
    private val interpreter: Interpreter<I, A, S>,
    private val processor: Processor<A, R, S, E>,
    private val reducer: Reducer<R, S>,
    private val timeMachineManager: TimeTravelManager,
    private val middlewares: List<Middleware<I, A, R, S, E>> = emptyList(),
    coroutineContext: CoroutineContext = Dispatchers.Main,
) : TimeTravelStore<I, S, E> {
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())
    private val intents = MutableSharedFlow<I>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)
    private val _entries = MutableSharedFlow<TimeMachineEntry>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)
    private val _state = MutableStateFlow(initialState)
    private val _events = MutableStateFlow(emptyList<E>())
    override val state: StateFlow<S> get() = _state
    override val currentState: S get() = _state.value
    override val event
        get() = _events.map { it.firstOrNull() }
            .applyEventMiddlewares(middlewares)
            .onEach { if (it != null) emitTimeMachineEntry(it) }
    override val entries: Flow<TimeMachineEntry> get() = _entries

    override fun process(id: Long) {
        TODO("Not yet implemented")
    }

    override fun debug(id: Long) {
        TODO("Not yet implemented")
    }

    init {
        scope.launch {
            timeMachineManager.attachStore(this@DefaultTimeTravelStore)
        }
        scope.launch {
            intents
                .applyIntentMiddlewares(middlewares)
                .onEach { emitTimeMachineEntry(it) }
                .mapNotNull { intent -> interpreter.interpret(intent, currentState) }
                .applyActionMiddlewares(middlewares)
                .onEach { emitTimeMachineEntry(it) }
                .flatMapMerge { action -> processor.process(action, currentState, ::send) }
                .applyResultMiddlewares(middlewares)
                .onEach { emitTimeMachineEntry(it) }
                .mapNotNull { result -> reducer.reduce(result, currentState) }
                .applyStateMiddlewares(middlewares)
                .onEach { emitTimeMachineEntry(it) }
                .collect { state -> _state.value = state }
        }
    }

    private suspend fun emitTimeMachineEntry(contract: Contract) {
        _entries.emit(TimeMachineEntry(this::class.toString(), contract, currentState))
    }

    override fun restoreState() {
    }

    override fun dispatch(intent: I) {
        scope.launch {
            if (timeMachineManager.currentState.mode != TimeMachineState.Mode.Stopped) {
                intents.emit(intent)
            }
        }
    }

    override fun process(event: E) {
        scope.launch { _events.emit(_events.value.filterNot { it == event }) }
    }

    private fun send(event: E) {
        scope.launch {
            if (timeMachineManager.currentState.mode != TimeMachineState.Mode.Stopped) {
                _events.emit(_events.value + event)
            }
        }
    }

    override fun dispose() {
        scope.launch {
            timeMachineManager.detachStore(this@DefaultTimeTravelStore)
        }
        scope.cancel()
    }

    override fun collect(
        onState: (S) -> Unit,
        onEvent: (E?) -> Unit,
    ): Job = scope.launch {
        launch { state.collect { onState(it) } }
        launch { event.collect { onEvent(it) } }
    }

    /**
     * Helper functions to apply [Middleware] to their intended stream
     */
    private fun Flow<I>.applyIntentMiddlewares(
        middlewares: List<Middleware<I, A, R, S, E>>,
    ): Flow<I> = middlewares.fold(this) { intents, middleware ->
        middleware.modifyIntents(intents, ::currentState)
    }

    private fun Flow<A>.applyActionMiddlewares(
        middlewares: List<Middleware<I, A, R, S, E>>,
    ): Flow<A> = middlewares.fold(this) { actions, middleware ->
        middleware.modifyActions(actions, ::currentState)
    }

    private fun Flow<R>.applyResultMiddlewares(
        middlewares: List<Middleware<I, A, R, S, E>>,
    ): Flow<R> = middlewares.fold(this) { results, middleware ->
        middleware.modifyResults(results, ::currentState)
    }

    private fun Flow<S>.applyStateMiddlewares(
        middlewares: List<Middleware<I, A, R, S, E>>,
    ): Flow<S> = middlewares.fold(this) { state, middleware ->
        middleware.modifyStates(state)
    }

    private fun Flow<E?>.applyEventMiddlewares(
        middlewares: List<Middleware<I, A, R, S, E>>,
    ): Flow<E?> = middlewares.fold(this) { events, middleware ->
        middleware.modifyEvents(events, ::currentState)
    }
}