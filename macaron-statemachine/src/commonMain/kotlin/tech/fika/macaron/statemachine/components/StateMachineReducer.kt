package tech.fika.macaron.statemachine.components

import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

class StateMachineReducer<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val stateMachine: StateMachine<I, A, R, S, E>,
) : Reducer<R, S> {
    override suspend fun reduce(result: R, state: S): S? = stateMachine.graph
        .filterKeys { key -> key.matches(state) }.values
        .flatMap { stateNode -> stateNode.results.entries }
        .find { resultMatcher -> resultMatcher.key.matches(result) }
        ?.value
        ?.invoke(StateMachine.ReducerNode(state, result))
}
