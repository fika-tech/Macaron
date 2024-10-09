package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition

data class ActionNode<A : Action, E : Event, S : State, A1 : A, S1 : S>(
    val action: A1,
    val state: S1,
    val dispatch: (A) -> Transition.Empty,
    val send: (E) -> Unit,
) {
    fun empty() = Transition.Empty
    fun invalid() = Transition.Invalid
    fun <S2 : S> transition(nextState: S1.() -> S2): Transition.Valid<A, S1, S2> =
        Transition.Valid(action = action, currentState = state, nextState = state.nextState())
}