package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

data class LifecycleNode<A : Action, S : State>(
    val onCreate: (ListenerNode<A, S>) -> Unit = {},
    val onStart: (ListenerNode<A, S>) -> Unit = {},
    val onResume: (ListenerNode<A, S>) -> Unit = {},
    val onPause: (ListenerNode<A, S>) -> Unit = {},
    val onStop: (ListenerNode<A, S>) -> Unit = {},
    val onDestroy: () -> Unit = {},
)