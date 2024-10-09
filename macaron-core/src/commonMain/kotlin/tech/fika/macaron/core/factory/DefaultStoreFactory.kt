package tech.fika.macaron.core.factory

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import tech.fika.macaron.core.store.DefaultStore
import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.lifecycle.LifecycleListener
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.StateListener
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import kotlin.coroutines.CoroutineContext

class DefaultStoreFactory : StoreFactory {
    override fun <A : Action, E : Event, S : State> create(
        initialState: S,
        processor: Processor<A, E, S>,
        lifecycle: Lifecycle?,
        stateListener: StateListener<A, S>?,
        lifecycleListener: LifecycleListener<A, S>?,
        interceptors: List<Interceptor<A, E, S>>,
        coroutineScope: CoroutineScope
    ): Store<A, E, S> = DefaultStore(
        initialState = initialState,
        processor = processor,
        lifecycle = lifecycle,
        coroutineScope = coroutineScope,
    ) {
        add(stateListener)
        add(lifecycleListener)
        add(interceptors)
    }
}
