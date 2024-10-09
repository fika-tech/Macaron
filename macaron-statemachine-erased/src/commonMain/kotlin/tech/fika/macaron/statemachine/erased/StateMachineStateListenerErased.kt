package tech.fika.macaron.statemachine.erased

import tech.fika.macaron.core.components.StateListener
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

class StateMachineStateListenerErased(
    private val stateMachine: StateMachineErased,
) : StateListener<Action, State> {
    private fun getNode(state: State) = stateMachine.stateMap.entries
        .find { stateMatcher -> stateMatcher.key.matches(value = state) }
        ?.value
        ?.stateListenerNode

    override fun onEnter(state: State, dispatch: (Action) -> Unit) =
        getNode(state = state)?.onEnter?.invoke(StateMachineErased.ListenerNode(state = state, dispatch = dispatch)) ?: Unit

    override fun onRepeat(state: State, dispatch: (Action) -> Unit) =
        getNode(state = state)?.onRepeat?.invoke(StateMachineErased.ListenerNode(state = state, dispatch = dispatch)) ?: Unit

    override fun onExit(state: State, dispatch: (Action) -> Unit) =
        getNode(state = state)?.onExit?.invoke(StateMachineErased.ListenerNode(state = state, dispatch = dispatch)) ?: Unit
}
