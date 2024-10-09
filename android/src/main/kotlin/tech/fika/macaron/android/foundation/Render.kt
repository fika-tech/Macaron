package tech.fika.macaron.android.foundation

import androidx.compose.runtime.Composable
import tech.fika.macaron.core.contract.State

@Composable
inline fun <reified S : State> State.render(block: @Composable S.() -> Unit) {
    if (this is S) {
        block(this)
    }
}
