package tech.fika.macaron.core.factory

import kotlin.coroutines.CoroutineContext
import tech.fika.macaron.core.components.DefaultStore
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.components.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

class DefaultStoreFactory : StoreFactory {
    override fun <I : Intent, A : Action, S : State> create(
        initialState: S,
        processor: Processor<I, A, S>,
        reducer: Reducer<A, S>,
        middlewares: List<Middleware<I, A, S>>,
        coroutineContext: CoroutineContext,
    ): Store<I, A, S> = DefaultStore(
        initialState = initialState,
        processor = processor,
        reducer = reducer,
        middlewares = middlewares,
        coroutineContext = coroutineContext
    )
}
