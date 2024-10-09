package tech.fika.macaron.logging

import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

class LoggingInterceptor<A : Action, E : Event, S : State>(
    private val logger: Logger = DefaultLogger(),
    private val tag: String = "Macaron",
    private val level: Logger.Level = Logger.Level.Debug,
) : Interceptor<A, E, S> {
    override fun interceptAction(action: A) {
        logger.log(level = level, tag = tag) { "Action: $action" }
    }

    override fun interceptEvent(event: E) {
        logger.log(level = level, tag = tag) { "Event: $event" }
    }

    override fun interceptState(state: S) {
        logger.log(level = level, tag = tag) { "State: $state" }
    }
}
