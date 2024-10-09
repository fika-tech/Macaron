package tech.fika.macaron.statesaver

import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

class StateSaverInterceptor<A : Action, E : Event, S : State>(
    private val stateSaver: StateSaver<S>,
) : Interceptor<A, E, S> {
    override fun interceptState(state: S) { stateSaver.save(state) }
}
