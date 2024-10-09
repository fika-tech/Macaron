package tech.fika.macaron.statemachine.builders

import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.statemachine.components.StateMachineDsl
import tech.fika.macaron.statemachine.nodes.InterceptorNode

@StateMachineDsl
class InterceptorBuilder<A : Action, E : Event, S : State> {
    private val interceptors = mutableListOf<Interceptor<A, E, S>>()

    fun add(interceptor: Interceptor<A, E, S>) {
        interceptors.add(interceptor)
    }

    fun build() = InterceptorNode(interceptors = interceptors)
}