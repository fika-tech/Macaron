package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

interface Interceptor<A : Action, E : Event, S : State> {
    fun interceptAction(action: A) = Unit
    fun interceptEvent(event: E) = Unit
    fun interceptState(state: S) = Unit
}
