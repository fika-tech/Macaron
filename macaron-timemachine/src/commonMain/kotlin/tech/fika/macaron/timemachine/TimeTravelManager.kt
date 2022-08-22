package tech.fika.macaron.timemachine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TimeTravelManager {
    val state: Flow<TimeMachineState>
    val currentState: TimeMachineState

    suspend fun attachStore(store: TimeTravelStore<*, *, *>)
    suspend fun detachStore(store: TimeTravelStore<*, *, *>)
}

class DefaultTimeTravelManager : TimeTravelManager {

    private val stores = HashMap<String, TimeTravelStore<*, *, *>>()
    private val _state = MutableStateFlow(TimeMachineState())
    override val state: Flow<TimeMachineState> get() = _state
    override val currentState: TimeMachineState get() = _state.value

    override suspend fun attachStore(store: TimeTravelStore<*, *, *>) {
        stores[store::class.toString()] = store
        store.entries.collect {
            if (currentState.mode == TimeMachineState.Mode.Recording) {
                _state.value = _state.value.copy(events = _state.value.events + it)
            }
        }
    }

    override suspend fun detachStore(store: TimeTravelStore<*, *, *>) {
        stores.remove(store::class.toString())
    }
}