package tech.fika.macaron.logging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

class LoggingMiddleware<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val logger: Logger,
    private val tag: String = "Macaron",
    private val level: Logger.Level = Logger.Level.Debug,
) : Middleware<I, A, R, S, E> {
    override fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = intents.onEach { intent ->
        logger.log(level = level, tag = tag) { "Intent: $intent" }
    }

    override fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions.onEach { action ->
        logger.log(level = level, tag = tag) { "Action: $action" }
    }

    override fun modifyResults(results: Flow<R>, state: () -> S): Flow<R> = results.onEach { result ->
        logger.log(level = level, tag = tag) { "Result: $result" }
    }

    override fun modifyStates(states: Flow<S>): Flow<S> = states.onEach { state ->
        logger.log(level = level, tag = tag) { "State: $state" }
    }

    override fun modifyEvents(events: Flow<E?>, state: () -> S): Flow<E?> = events.onEach { event ->
        if (event != null) logger.log(level = level, tag = tag) { "Event: $event" }
    }
}
