package tech.fika.macaron.statemachine.erased

import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition
import tech.fika.macaron.statemachine.builders.InterceptorBuilder
import tech.fika.macaron.statemachine.nodes.InterceptorNode
import tech.fika.macaron.statemachine.builders.Matcher
import tech.fika.macaron.statemachine.components.StateDsl
import tech.fika.macaron.statemachine.components.StateMachineDsl

@Suppress("UNCHECKED_CAST")
class StateMachineErased(
    private val rootNode: RootNode
) {
    constructor(builder: Builder.() -> Unit) : this(
        Builder().apply(builder).build().rootNode
    )

    val initialState: State get() = rootNode.initialState ?: error("Initial State is not set")
    val processor: Processor<Action, Event, State> get() = StateMachineProcessorErased(stateMachine = this)
    val stateMap: Map<Matcher<State, State>, StateNode> = rootNode.stateMap
    val lifecycleNode = rootNode.lifecycleNode
    val interceptors = rootNode.interceptorNode.interceptors
    
    /* Nodes */
    data class RootNode(
        internal val initialState: State?,
        internal val stateMap: Map<Matcher<State, State>, StateNode>,
        internal val lifecycleNode: LifecycleErasedNode,
        internal val interceptorNode: InterceptorNode<Action, Event, State>,
    )

    data class ListenerNode<A : Action, S : State>(
        val state: S,
        val dispatch: (A) -> Unit,
    )

    data class StateListenerNode<A : Action, S : State>(
        val onEnter: (ListenerNode<A, S>) -> Unit,
        val onRepeat: (ListenerNode<A, S>) -> Unit,
        val onExit: (ListenerNode<A, S>) -> Unit,
    )

    data class StateNode(
        val actionMap: Map<Matcher<Action, Action>, (ActionNode<Action, State>) -> Transition<Action, State, State>>,
        val stateListenerNode: StateListenerNode<Action, State>,
    )

    data class ActionNode<A : Action, S : State>(
        val action: A,
        val state: S,
        val dispatch: (Action) -> Transition.Empty,
        val send: (Event) -> Unit,
    ) {
        fun empty() = Transition.Empty
        fun invalid() = Transition.Invalid
        fun <S1 : State> transition(nextState: S.() -> S1): Transition.Valid<Action, S, S1> =
            Transition.Valid(action = action, currentState = state, nextState = state.nextState())
    }

    @StateMachineDsl
    class Builder {
        private var interceptorNode: InterceptorNode<Action, Event, State> = InterceptorNode()
        private var lifecycleNode: LifecycleErasedNode = LifecycleErasedNode()
        private val stateMap = LinkedHashMap<Matcher<State, State>, StateNode>()
        private var initialState: State? = null

        fun initialState(block: @StateMachineDsl () -> State) {
            initialState = block()
        }

        fun <S : State> state(stateMatcher: Matcher<State, S>, builder: @StateMachineDsl StateBuilder<S>.() -> Unit) {
            stateMap[stateMatcher] = StateBuilder<S>().apply(builder).build()
        }

        inline fun <reified S : State> state(noinline builder: @StateMachineDsl StateBuilder<S>.() -> Unit) = state(Matcher.any(), builder)

        fun lifecycle(builder: @StateMachineDsl LifecycleErasedBuilder.() -> Unit) {
            lifecycleNode = LifecycleErasedBuilder().apply(builder).build()
        }

        fun interceptors(builder: @StateMachineDsl InterceptorBuilder<Action, Event, State>.() -> Unit) {
            interceptorNode = InterceptorBuilder<Action, Event, State>().apply(builder).build()
        }

        internal fun build(): StateMachineErased = StateMachineErased(
            rootNode = RootNode(
                initialState = initialState,
                stateMap = stateMap.toMap(),
                lifecycleNode = lifecycleNode,
                interceptorNode = interceptorNode,
            )
        )

        @StateDsl
        inner class StateBuilder<S : State> {
            private val actionMap = mutableMapOf<Matcher<Action, Action>, (ActionNode<Action, State>) -> Transition<Action, State, State>>()
            private var onEnter: (ListenerNode<Action, S>) -> Unit = {}
            private var onRepeat: (ListenerNode<Action, S>) -> Unit = {}
            private var onExit: (ListenerNode<Action, S>) -> Unit = {}

            fun onEnter(block: @StateDsl ListenerNode<Action, S>.() -> Unit) {
                onEnter = block
            }

            fun onRepeat(block: @StateDsl ListenerNode<Action, S>.() -> Unit) {
                onRepeat = block
            }

            fun onExit(block: @StateDsl ListenerNode<Action, S>.() -> Unit) {
                onExit = block
            }

            fun <A : Action> process(actionMatcher: Matcher<Action, A>, process: @StateDsl ActionNode<A, S>.() -> Transition<Action, S, State>) {
                actionMap[actionMatcher] = { (action, currentState, dispatch, send) ->
                    process(
                        ActionNode<A, State>(
                            action = action as A,
                            state = currentState,
                            dispatch = dispatch,
                            send = send,
                        ) as ActionNode<A, S>
                    )
                }
            }

            inline fun <reified A : Action> process(noinline action: @StateDsl ActionNode<A, S>.() -> Transition<Action, S, State>) =
                process(Matcher.any(), action)

            internal fun build(): StateNode = StateNode(
                actionMap = actionMap,
                stateListenerNode = StateListenerNode(
                    onEnter = onEnter as (ListenerNode<Action, State>) -> Unit,
                    onRepeat = onRepeat as (ListenerNode<Action, State>) -> Unit,
                    onExit = onExit as (ListenerNode<Action, State>) -> Unit
                )
            )
        }
    }
}

