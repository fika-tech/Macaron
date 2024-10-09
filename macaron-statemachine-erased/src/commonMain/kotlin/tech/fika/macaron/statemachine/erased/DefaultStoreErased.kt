package tech.fika.macaron.statemachine.erased

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
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.store.StoreConfiguration

class DefaultStoreErased(
    initialState: State,
    private val processor: Processor<Action, Event, State>,
    override val lifecycle: Lifecycle? = null,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    storeConfiguration: StoreConfiguration.Builder<Action, Event, State>.() -> Unit,
) : Store<Action, Event, State> {
    private val mutex: Mutex = Mutex()
    private val stateFlow = MutableStateFlow(initialState)
    private val eventFlow = MutableStateFlow(emptyList<Event>())
    override val state = stateFlow
    override val currentState: State get() = stateFlow.value
    override val event = eventFlow.map { it.firstOrNull() }
    private val configuration = StoreConfiguration.Builder<Action, Event, State>().apply(storeConfiguration).build()
    private val lifecycleCallbacks = configuration.lifecycleListener?.toLifecycleCallbacks(
        getState = { currentState },
        dispatch = ::dispatch
    )

    init {
        if (lifecycle != null && lifecycleCallbacks != null) {
            lifecycle.subscribe(lifecycleCallbacks)
        }
    }

    override fun dispatch(action: Action) {
        coroutineScope.launch {
            mutex.withLock {
                configuration.interceptors.forEach { it.interceptAction(action) }
                val transition = processor.process(
                    action = action,
                    state = currentState,
                    send = ::send,
                    dispatch = ::dispatch
                )
                val nextState = if (transition is Valid<Action, State, State>) {
                    configuration.stateListener?.let { stateListener ->
                        if (currentState::class != transition.nextState::class) {
                            stateListener.onExit(state = transition.currentState, dispatch = ::dispatch)
                            stateListener.onEnter(state = transition.nextState, dispatch = ::dispatch)
                        } else {
                            stateListener.onRepeat(state = transition.nextState, dispatch = ::dispatch)
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

    override fun process(event: Event) {
        coroutineScope.launch { eventFlow.emit(eventFlow.value.filterNot { it == event }) }
    }

    private fun send(event: Event) {
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
        onState: (State) -> Unit,
        onEvent: (Event?) -> Unit,
    ): Job = coroutineScope.launch {
        coroutineScope.launch { state.collect { onState(it) } }
        coroutineScope.launch { event.collect { onEvent(it) } }
    }
}
