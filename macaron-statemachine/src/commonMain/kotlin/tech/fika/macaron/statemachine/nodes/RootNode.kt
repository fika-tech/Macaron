package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.statemachine.builders.Matcher

data class RootNode<A : Action, E : Event, S : State>(
    internal val initialState: S?,
    internal val stateMap: Map<Matcher<S, S>, StateNode<A, E, S>>,
    internal val lifecycleNode: LifecycleNode<A, S>,
    internal val interceptorNode: InterceptorNode<A, E, S>,
)