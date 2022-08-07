package tech.fika.macaron.messaging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Message

interface MessageManager {
    val messages: Flow<Message>
    suspend fun send(message: Message)
}

class DefaultMessageManager : MessageManager {
    private val internalMessages = MutableSharedFlow<Message>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)
    override val messages: Flow<Message> get() = internalMessages

    override suspend fun send(message: Message) {
        internalMessages.emit(message)
    }
}
