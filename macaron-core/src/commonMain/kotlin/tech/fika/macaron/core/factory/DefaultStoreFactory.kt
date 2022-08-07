package tech.fika.macaron.core.factory

import kotlin.coroutines.CoroutineContext
import tech.fika.macaron.core.components.DefaultStore
import tech.fika.macaron.core.components.Interpreter
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.components.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

class DefaultStoreFactory : StoreFactory {
    override fun <I : Intent, A : Action, R : Result, S : State, E : Event> create(
        initialState: S,
        interpreter: Interpreter<I, A, S>,
        processor: Processor<A, R, S, E>,
        reducer: Reducer<R, S>,
        middlewares: List<Middleware<I, A, R, S, E>>,
        coroutineContext: CoroutineContext,
    ): Store<I, S, E> = DefaultStore(
        initialState = initialState,
        interpreter = interpreter,
        processor = processor,
        reducer = reducer,
        middlewares = middlewares,
        coroutineContext = coroutineContext
    )
}
