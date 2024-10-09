package tech.fika.macaron.statemachine.components

import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.statemachine.builders.Matcher
import tech.fika.macaron.statemachine.nodes.RootNode
import tech.fika.macaron.statemachine.builders.StateMachineBuilder
import tech.fika.macaron.statemachine.nodes.StateNode

class StateMachine<A : Action, E : Event, S : State>(
    private val rootNode: RootNode<A, E, S>,
) {
    constructor(builder: StateMachineBuilder<A, E, S>.() -> Unit) : this(
        StateMachineBuilder<A, E, S>().apply(builder).build().rootNode
    )

    val initialState: S get() = rootNode.initialState ?: error("Initial State is not set")
    val stateMap: Map<Matcher<S, S>, StateNode<A, E, S>> = rootNode.stateMap
    val processor: Processor<A, E, S> get() = StateMachineProcessor(stateMachine = this)
    val lifecycleNode = rootNode.lifecycleNode
    val interceptors = rootNode.interceptorNode.interceptors
}
