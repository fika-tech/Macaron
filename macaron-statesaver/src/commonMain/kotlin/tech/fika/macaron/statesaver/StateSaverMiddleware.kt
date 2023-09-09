package tech.fika.macaron.statesaver

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

class StateSaverMiddleware<I : Intent, A : Action, S : State>(
    private val stateSaver: StateSaver<S>,
) : Middleware<I, A, S> {
    override fun modifyStates(states: Flow<S>): Flow<S> = states.onEach(stateSaver::save)
}
