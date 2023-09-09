package tech.fika.macaron.core.factory

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.components.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

interface StoreFactory {
    fun <I : Intent, A : Action, S : State> create(
        initialState: S,
        processor: Processor<I, A, S>,
        reducer: Reducer<A, S>,
        middlewares: List<Middleware<I, A, S>> = emptyList(),
        coroutineContext: CoroutineContext = Dispatchers.Main.immediate,
    ): Store<I, A, S>

    fun <I : Intent, A : Action, S : State> create(
        initialState: S,
        processor: Processor<I, A, S>,
        reducer: Reducer<A, S>,
        middlewares: List<Middleware<I, A, S>> = emptyList(),
    ): Store<I, A, S> = create(
        initialState = initialState,
        processor = processor,
        reducer = reducer,
        middlewares = middlewares,
        coroutineContext = Dispatchers.Main.immediate
    )

    fun <I : Intent, A : Action, S : State> create(
        initialState: S,
        processor: Processor<I, A, S>,
        reducer: Reducer<A, S>,
    ): Store<I, A, S> = create(
        initialState = initialState,
        processor = processor,
        reducer = reducer,
        middlewares = emptyList(),
        coroutineContext = Dispatchers.Main.immediate
    )
}