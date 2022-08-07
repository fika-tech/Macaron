package tech.fika.macaron.statemachine.utils

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.factory.StoreFactory
import tech.fika.macaron.statemachine.components.StateMachine

fun <I : Intent, A : Action, R : Result, S : State, E : Event> StoreFactory.create(
    initialState: S,
    stateMachine: StateMachine<I, A, R, S, E>,
    middlewares: List<Middleware<I, A, R, S, E>> = emptyList(),
    coroutineContext: CoroutineContext = Dispatchers.Main,
): Store<I, S, E> = create(
    initialState = initialState,
    interpreter = stateMachine.interpreter,
    processor = stateMachine.processor,
    reducer = stateMachine.reducer,
    middlewares = middlewares,
    coroutineContext = coroutineContext
)
