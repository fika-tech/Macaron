package tech.fika.macaron.statemachine.erased

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.store.StoreConfiguration
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.statemachine.components.StateMachine
import tech.fika.macaron.statemachine.components.StateMachineStateListener

fun stateMachineErased(
    builder: StateMachineErased.Builder.() -> Unit
) = StateMachineErased.Builder().apply(builder).build()

fun StateMachineErased.store(
    initialState: State? = null,
    lifecycle: Lifecycle? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    storeConfiguration: StoreConfiguration.Builder<Action, Event, State>.() -> Unit = {}
): Store<Action, Event, State> = DefaultStoreErased(
    initialState = initialState ?: this.initialState,
    processor = processor,
    lifecycle = lifecycle,
    coroutineScope = coroutineScope,
) {
    add(StateMachineStateListenerErased(this@store))
    add(StateMachineLifecycleListenerErased(this@store))
    add(interceptors)
    storeConfiguration()
}
