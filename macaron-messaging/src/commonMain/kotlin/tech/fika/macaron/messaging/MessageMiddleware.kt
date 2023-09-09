package tech.fika.macaron.messaging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

open class MessageSenderMiddleware<I : Intent, A : Action, S : State>(
    private val messageManager: MessageManager,
    private val sendAction: ((A) -> Message?)? = null,
) : Middleware<I, A, S> {
    override fun modifyActions(actions: Flow<A>, state: () -> S): Flow<A> = actions.onEach { action ->
        sendAction?.invoke(action)?.let { messageManager.send(it) }
    }
}

open class MessageReceiverMiddleware<I : Intent, A : Action, S : State>(
    private val messageManager: MessageManager,
    private val receive: (Message) -> I?,
) : Middleware<I, A, S> {
    override fun modifyIntents(intents: Flow<I>, state: () -> S): Flow<I> = merge(
        intents, messageManager.messages.map(receive).filterNotNull()
    )
}
