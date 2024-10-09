package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

data class StateListenerNode<A : Action, S : State>(
    val onEnter: (ListenerNode<A, S>) -> Unit,
    val onRepeat: (ListenerNode<A, S>) -> Unit,
    val onExit: (ListenerNode<A, S>) -> Unit,
)