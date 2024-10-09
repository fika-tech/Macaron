package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

data class ListenerNode<A : Action, S : State>(
    val state: S,
    val dispatch: (A) -> Unit,
)