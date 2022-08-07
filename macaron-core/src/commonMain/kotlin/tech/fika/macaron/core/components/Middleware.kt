package tech.fika.macaron.core.components

import kotlinx.coroutines.flow.Flow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

interface Middleware<I : Intent, A : Action, R : Result, S : State, E : Event> {
    fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = intents
    fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions
    fun modifyResults(results: Flow<R>, state: () -> S): Flow<R> = results
    fun modifyStates(states: Flow<S>): Flow<S> = states
    fun modifyEvents(events: Flow<E?>, state: () -> S): Flow<E?> = events
}

class EmptyMiddleware<I : Intent, A : Action, R : Result, S : State, E : Event> : Middleware<I, A, R, S, E>