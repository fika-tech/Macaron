package tech.fika.macaron.statemachine.components

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.store.DefaultStore
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.store.StoreConfiguration
import tech.fika.macaron.statemachine.builders.StateMachineBuilder

fun <A : Action, E : Event, S : State> stateMachine(
    builder: StateMachineBuilder<A, E, S>.() -> Unit
) = StateMachineBuilder<A, E, S>().apply(builder).build()


fun <A : Action, E : Event, S : State> StateMachine<A, E, S>.store(
    initialState: S? = null,
    lifecycle: Lifecycle? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    storeConfiguration: StoreConfiguration.Builder<A, E, S>.() -> Unit = {}
): Store<A, E, S> = DefaultStore(
    initialState = initialState ?: this.initialState,
    processor = processor,
    lifecycle = lifecycle,
    coroutineScope = coroutineScope,
) {
    add(StateMachineStateListener(this@store))
    add(StateMachineLifecycleListener(this@store))
    add(interceptors)
    storeConfiguration()
}
