package tech.fika.macaron.statemachine.components

import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

class StateMachineReducer<I : Intent, A : Action, S : State>(
    private val stateMachine: StateMachine<I, A, S>,
) : Reducer<A, S> {
    override suspend fun reduce(action: A, state: S): S = stateMachine.stateMap
        .filterKeys { key -> key.matches(state) }.values
        .flatMap { stateNode -> stateNode.actionMap.entries }
        .find { actionMatcher -> actionMatcher.key.matches(action) }
        ?.value
        ?.invoke(StateMachine.ActionNode(action = action, state = state))
        ?: state
}
