package tech.fika.macaron.android.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

data class Contract<A : Action, E : Event, S : State>(
    val state: S,
    val dispatch: (A) -> Unit = {},
    internal val event: E? = null,
    internal val process: (E) -> Unit = {},
)

@Composable
fun <A : Action, E: Event, S : State> contract(
    store: Store<A, E, S>,
): Contract<A, E, S> {
    val state by store.state.collectAsState()
    val event by store.event.collectAsState(initial = null)

    return Contract(
        state = state,
        dispatch = store::dispatch,
        event = event,
        process = store::process
    )
}
