package tech.fika.macaron.statemachine.nodes

import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State


data class InterceptorNode<A : Action, E : Event, S : State>(
    val interceptors: List<Interceptor<A, E, S>> = emptyList()
)