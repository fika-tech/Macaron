package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition
import tech.fika.macaron.statemachine.builders.Matcher

data class StateNode<A : Action, E : Event, S : State>(
    val actionMap: Map<Matcher<A, A>, (ActionNode<A, E, S, A, S>) -> Transition<A, S, S>>,
    val stateListenerNode: StateListenerNode<A, S>,
)