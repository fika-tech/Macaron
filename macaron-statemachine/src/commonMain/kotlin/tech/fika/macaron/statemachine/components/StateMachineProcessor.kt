package tech.fika.macaron.statemachine.components

import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition
import tech.fika.macaron.statemachine.nodes.ActionNode

class StateMachineProcessor<A : Action, E : Event, S : State>(
    private val stateMachine: StateMachine<A, E, S>,
) : Processor<A, E, S> {
    override fun process(
        action: A,
        state: S,
        dispatch: (A) -> Unit,
        send: (E) -> Unit,
    ): Transition<A, S, S> = stateMachine.stateMap
        .filterKeys { key -> key.matches(value = state) }
        .values
        .flatMap { stateNode -> stateNode.actionMap.entries }
        .find { actionMatcher -> actionMatcher.key.matches(value = action) }
        ?.value
        ?.invoke(
            ActionNode(
                action = action,
                state = state,
                dispatch = {
                    dispatch(it)
                    Transition.Empty
                },
                send = send
            )
        )
        ?: Transition.Invalid
}
