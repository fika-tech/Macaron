package tech.fika.macaron.logging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

class LoggingMiddleware<I : Intent, A : Action, S : State>(
    private val logger: Logger,
    private val tag: String = "Macaron",
    private val level: Logger.Level = Logger.Level.Debug,
) : Middleware<I, A, S> {
    override fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = intents.onEach { intent ->
        logger.log(level = level, tag = tag) { "Intent: $intent" }
    }

    override fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions.onEach { action ->
        logger.log(level = level, tag = tag) {
            when (action) {
                is Action.Event -> "Event: $action"
                is Action.Message -> "Message: $action"
                else -> "Action: $action"
            }
        }
    }

    override fun modifyStates(states: Flow<S>): Flow<S> = states.onEach { state ->
        logger.log(level = level, tag = tag) { "State: $state" }
    }
}
