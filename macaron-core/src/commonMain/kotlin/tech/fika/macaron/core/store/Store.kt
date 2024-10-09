package tech.fika.macaron.core.store

import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

interface Store<A : Action, E : Event, S : State> {
    /**
     * Stream of [State] exposed to the client
     */
    val state: StateFlow<S>

    /**
     * Stream of most recent [Event] exposed to the client
     */
    val event: Flow<E?>

    /**
     * Current [State] of the store
     */
    val currentState: S

    val lifecycle: Lifecycle?

    /**
     * Dispatches an [Action]
     */
    fun dispatch(action: A)

    /**
     * Processes an [Event]
     */
    fun process(event: E)

    /**
     * Cancels all jobs within the store
     */
    fun dispose()

    /**
     * Collect used for Kotlin Native
     */
    fun collect(
        onState: (S) -> Unit,
        onEvent: (E?) -> Unit,
    ): Job
}
