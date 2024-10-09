package tech.fika.macaron.core.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface Message

interface MessageManager {
    fun send(message: Message)
    fun subscribe(onMessage: (Message) -> Unit)
    fun dispose()
}

class DefaultMessageManager(
    override val coroutineContext: CoroutineContext = Dispatchers.Default
) : MessageManager, CoroutineScope {
    private val messages = MutableSharedFlow<Message>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)

    override fun send(message: Message) {
        launch {
            messages.emit(message)
        }
    }

    override fun subscribe(onMessage: (Message) -> Unit) {
        launch {
            messages.collect(onMessage)
        }
    }

    override fun dispose() = cancel()
}
