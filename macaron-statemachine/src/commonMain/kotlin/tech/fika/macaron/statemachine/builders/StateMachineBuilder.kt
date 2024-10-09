package tech.fika.macaron.statemachine.builders

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition
import tech.fika.macaron.statemachine.components.StateDsl
import tech.fika.macaron.statemachine.components.StateMachine
import tech.fika.macaron.statemachine.components.StateMachineDsl
import tech.fika.macaron.statemachine.nodes.ActionNode
import tech.fika.macaron.statemachine.nodes.InterceptorNode
import tech.fika.macaron.statemachine.nodes.LifecycleNode
import tech.fika.macaron.statemachine.nodes.ListenerNode
import tech.fika.macaron.statemachine.nodes.RootNode
import tech.fika.macaron.statemachine.nodes.StateListenerNode
import tech.fika.macaron.statemachine.nodes.StateNode

@StateMachineDsl
class StateMachineBuilder<A : Action, E : Event, S : State> {
    private var interceptorNode: InterceptorNode<A, E, S> = InterceptorNode()
    private var lifecycleNode: LifecycleNode<A, S> = LifecycleNode()
    private val stateMap = LinkedHashMap<Matcher<S, S>, StateNode<A, E, S>>()
    private var initialState: S? = null

    fun initialState(block: @StateMachineDsl () -> S) {
        initialState = block()
    }

    fun <S1 : S> state(stateMatcher: Matcher<S, S1>, builder: @StateMachineDsl StateBuilder<S1>.() -> Unit) {
        stateMap[stateMatcher] = StateBuilder<S1>().apply(builder).build()
    }

    inline fun <reified S1 : S> state(noinline builder: @StateMachineDsl StateBuilder<S1>.() -> Unit) = state(Matcher.any(), builder)

    fun lifecycle(builder: @StateMachineDsl LifecycleBuilder<A, S>.() -> Unit) {
        lifecycleNode = LifecycleBuilder<A, S>().apply(builder).build()
    }

    fun interceptors(builder: @StateMachineDsl InterceptorBuilder<A, E, S>.() -> Unit) {
        interceptorNode = InterceptorBuilder<A, E, S>().apply(builder).build()
    }

    internal fun build(): StateMachine<A, E, S> = StateMachine(
        rootNode = RootNode(
            initialState = initialState,
            stateMap = stateMap.toMap(),
            lifecycleNode = lifecycleNode,
            interceptorNode = interceptorNode,
        )
    )

    @Suppress("UNCHECKED_CAST")
    @StateDsl
    inner class StateBuilder<S1 : S> {
        private val actionMap = mutableMapOf<Matcher<A, A>, (ActionNode<A, E, S, A, S>) -> Transition<A, S, S>>()
        private var onEnter: (ListenerNode<A, S1>) -> Unit = {}
        private var onRepeat: (ListenerNode<A, S1>) -> Unit = {}
        private var onExit: (ListenerNode<A, S1>) -> Unit = {}

        fun onEnter(block: @StateDsl ListenerNode<A, S1>.() -> Unit) {
            onEnter = block
        }

        fun onRepeat(block: @StateDsl ListenerNode<A, S1>.() -> Unit) {
            onRepeat = block
        }

        fun onExit(block: @StateDsl ListenerNode<A, S1>.() -> Unit) {
            onExit = block
        }

        fun <A1 : A> process(actionMatcher: Matcher<A, A1>, process: @StateDsl ActionNode<A, E, S, A1, S1>.() -> Transition<A, S1, S>) {
            actionMap[actionMatcher] = { (action, currentState, dispatch, send) ->
                process(
                    ActionNode<A, E, S, A1, S>(
                        action = action as A1,
                        state = currentState,
                        dispatch = dispatch,
                        send = send,
                    ) as ActionNode<A, E, S, A1, S1>
                )
            }
        }

        inline fun <reified A1 : A> process(noinline action: @StateDsl ActionNode<A, E, S, A1, S1>.() -> Transition<A, S1, S>) =
            process(Matcher.any(), action)

        internal fun build(): StateNode<A, E, S> = StateNode(
            actionMap = actionMap,
            stateListenerNode = StateListenerNode(
                onEnter = onEnter as (ListenerNode<A, S>) -> Unit,
                onRepeat = onRepeat as (ListenerNode<A, S>) -> Unit,
                onExit = onExit as (ListenerNode<A, S>) -> Unit
            )
        )
    }
}