package tech.fika.macaron.android.foundation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import tech.fika.macaron.core.contract.Event

@SuppressLint("ComposableNaming")
@Composable
fun <E : Event> E.handle(process: (E) -> Unit, block: CoroutineScope.(E) -> Unit) {
    val event = this
    LaunchedEffect(this) {
        block(event)
        process(event)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal inline fun <reified E : Event> Contract<*, E, *>.handleEvents(noinline block: CoroutineScope.(E) -> Unit) {
    event?.handle(process = process, block = block)
}
