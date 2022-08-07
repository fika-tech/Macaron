package tech.fika.macaron.core.factory

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
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

interface StoreFactory {
    fun <I : Intent, A : Action, R : Result, S : State, E : Event> create(
        initialState: S,
        interpreter: Interpreter<I, A, S>,
        processor: Processor<A, R, S, E>,
        reducer: Reducer<R, S>,
        middlewares: List<Middleware<I, A, R, S, E>> = emptyList(),
        coroutineContext: CoroutineContext = Dispatchers.Main,
    ): Store<I, S, E>

    fun <I : Intent, A : Action, R : Result, S : State, E : Event> create(
        initialState: S,
        interpreter: Interpreter<I, A, S>,
        processor: Processor<A, R, S, E>,
        reducer: Reducer<R, S>,
        middlewares: List<Middleware<I, A, R, S, E>> = emptyList(),
    ): Store<I, S, E> = create(
        initialState = initialState,
        interpreter = interpreter,
        processor = processor,
        reducer = reducer,
        middlewares = middlewares,
        coroutineContext = Dispatchers.Main
    )

    fun <I : Intent, A : Action, R : Result, S : State, E : Event> create(
        initialState: S,
        interpreter: Interpreter<I, A, S>,
        processor: Processor<A, R, S, E>,
        reducer: Reducer<R, S>,
    ): Store<I, S, E> = create(
        initialState = initialState,
        interpreter = interpreter,
        processor = processor,
        reducer = reducer,
        middlewares = emptyList(),
        coroutineContext = Dispatchers.Main
    )
}
