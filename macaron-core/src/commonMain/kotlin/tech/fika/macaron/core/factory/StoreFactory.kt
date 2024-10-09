package tech.fika.macaron.core.factory

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.StateListener
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.lifecycle.LifecycleListener
import tech.fika.macaron.core.store.Store

interface StoreFactory {
    fun <A : Action, E : Event, S : State> create(
        initialState: S,
        processor: Processor<A, E, S>,
        lifecycle: Lifecycle? = null,
        stateListener: StateListener<A, S>? = null,
        lifecycleListener: LifecycleListener<A, S>? = null,
        interceptors: List<Interceptor<A, E, S>> = emptyList(),
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    ): Store<A, E, S>

    fun <A : Action, E : Event, S : State> create(
        initialState: S,
        processor: Processor<A, E, S>,
        interceptors: List<Interceptor<A, E, S>> = emptyList(),
    ): Store<A, E, S> = create(
        initialState = initialState,
        processor = processor,
        interceptors = interceptors,
    )

    fun <A : Action, E : Event, S : State> create(
        initialState: S,
        processor: Processor<A, E, S>,
    ): Store<A, E, S> = create(
        initialState = initialState,
        processor = processor,
    )
}