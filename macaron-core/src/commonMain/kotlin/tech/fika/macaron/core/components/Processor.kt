package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.contract.Transition

fun interface Processor<A : Action, E : Event, S : State> {
    fun process(action: A, state: S, dispatch: (A) -> Unit, send: (E) -> Unit): Transition<A, S, S>
}
