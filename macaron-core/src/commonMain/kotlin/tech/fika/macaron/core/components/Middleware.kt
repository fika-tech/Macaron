package tech.fika.macaron.core.components

import kotlinx.coroutines.flow.Flow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

interface Middleware<I : Intent, A : Action, S : State> {
    fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = intents
    fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions
    fun modifyStates(states: Flow<S>): Flow<S> = states
}
