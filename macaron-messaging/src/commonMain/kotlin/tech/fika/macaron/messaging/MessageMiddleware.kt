package tech.fika.macaron.messaging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

open class MessageSenderMiddleware<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val messageManager: MessageManager,
    private val sendAction: ((A) -> Message?)? = null,
    private val sendResult: ((R) -> Message?)? = null,
) : Middleware<I, A, R, S, E> {
    override fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions.onEach { action ->
        sendAction?.invoke(action)?.let { messageManager.send(it) }
    }

    override fun modifyResults(results: Flow<R>, state: () -> S): Flow<R> = results.onEach { result ->
        sendResult?.invoke(result)?.let { messageManager.send(it) }
    }
}

open class MessageReceiverMiddleware<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val messageManager: MessageManager,
    private val receive: (Message) -> I?,
) : Middleware<I, A, R, S, E> {
    override fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = merge(
        intents, messageManager.messages.map(receive).filterNotNull()
    )
}
