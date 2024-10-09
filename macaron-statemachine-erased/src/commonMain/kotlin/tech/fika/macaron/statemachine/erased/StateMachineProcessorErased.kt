package tech.fika.macaron.statemachine.erased

import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition

class StateMachineProcessorErased(
    private val stateMachine: StateMachineErased,
) : Processor<Action, Event, State> {
    override fun process(
        action: Action,
        state: State,
        dispatch: (Action) -> Unit,
        send: (Event) -> Unit,
    ): Transition<Action, State, State> = stateMachine.stateMap
        .filterKeys { key -> key.matches(value = state) }
        .values
        .flatMap { stateNode -> stateNode.actionMap.entries }
        .find { actionMatcher -> actionMatcher.key.matches(value = action) }
        ?.value
        ?.invoke(
            StateMachineErased.ActionNode(
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
