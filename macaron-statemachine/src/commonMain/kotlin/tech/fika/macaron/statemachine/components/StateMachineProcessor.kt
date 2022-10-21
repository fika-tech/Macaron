package tech.fika.macaron.statemachine.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

class StateMachineProcessor<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val stateMachine: StateMachine<I, A, R, S, E>,
) : Processor<A, R, S, E> {
    override suspend fun process(action: A, state: S, send: (E) -> Unit): Flow<R> = stateMachine.graph
        .filterKeys { key -> key.matches(state) }.values
        .flatMap { stateNode -> stateNode.actions.entries }
        .find { actionMatcher -> actionMatcher.key.matches(action) }
        ?.value
        ?.invoke(StateMachine.ProcessorNode(state, action))
        ?.onEach { node -> if (node is StateMachine.SideEffect.EventNode<*, E>) send(node.value) }
        ?.filterIsInstance<StateMachine.SideEffect.ResultNode<R, *>>()
        ?.map { node -> node.value }
        ?.filterNotNull() ?: flowOf()
}
