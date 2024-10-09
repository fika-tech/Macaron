package tech.fika.macaron.core.store

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition.Valid

class DefaultStore<A : Action, E : Event, S : State>(
    initialState: S,
    private val processor: Processor<A, E, S>,
    override val lifecycle: Lifecycle? = null,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    storeConfiguration: StoreConfiguration.Builder<A, E, S>.() -> Unit,
) : Store<A, E, S> {
    private val mutex: Mutex = Mutex()
    private val stateFlow = MutableStateFlow(initialState)
    private val eventFlow = MutableStateFlow(emptyList<E>())
    private val configuration = StoreConfiguration.Builder<A, E, S>().apply(storeConfiguration).build()
    private val lifecycleCallbacks = configuration.lifecycleListener?.toLifecycleCallbacks(
        getState = { currentState },
        dispatch = ::dispatch
    )

    override val state = stateFlow
    override val currentState: S get() = stateFlow.value
    override val event = eventFlow.map { it.firstOrNull() }

    init {
        if (lifecycle != null && lifecycleCallbacks != null) {
            lifecycle.subscribe(lifecycleCallbacks)
        }
    }

    override fun dispatch(action: A) {
        coroutineScope.launch {
            mutex.withLock {
                configuration.interceptors.forEach { it.interceptAction(action) }
                val transition = processor.process(
                    action = action,
                    state = currentState,
                    send = ::send,
                    dispatch = ::dispatch
                )
                val nextState = if (transition is Valid<A, S, S>) {
                    if (configuration.stateListener != null) {
                        if (transition.currentState::class != transition.nextState::class) {
                            configuration.stateListener.onExit(state = transition.currentState, dispatch = ::dispatch)
                            configuration.stateListener.onEnter(state = transition.nextState, dispatch = ::dispatch)
                        } else {
                            configuration.stateListener.onRepeat(state = transition.nextState, dispatch = ::dispatch)
                        }
                    }
                    transition.nextState
                } else {
                    currentState
                }
                configuration.interceptors.forEach { it.interceptState(nextState) }
                stateFlow.update { nextState }
            }
        }
    }

    override fun process(event: E) {
        coroutineScope.launch { eventFlow.emit(eventFlow.value.filterNot { it == event }) }
    }

    private fun send(event: E) {
        configuration.interceptors.forEach { it.interceptEvent(event) }
        coroutineScope.launch { eventFlow.emit(eventFlow.value + event) }
    }

    override fun dispose() {
        if (lifecycle != null && lifecycleCallbacks != null) {
            lifecycle.unsubscribe(lifecycleCallbacks)
        }
        coroutineScope.cancel()
    }

    override fun collect(
        onState: (S) -> Unit,
        onEvent: (E?) -> Unit,
    ): Job = coroutineScope.launch {
        launch { state.collect { onState(it) } }
        launch { event.collect { onEvent(it) } }
    }
}
