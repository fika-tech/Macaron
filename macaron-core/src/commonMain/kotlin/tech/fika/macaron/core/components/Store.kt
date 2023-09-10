package tech.fika.macaron.core.components

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

/**
 * The store exposes a stream of [State] and a stream of [Event] for the client to react to.
 */
interface Store<I : Intent, A : Action, S : State> {
    /**
     * Stream of [State] exposed to the client
     */
    val state: StateFlow<S>

    /**
     * Stream of most recent [Event] exposed to the client
     */
    val event: Flow<A?>

    /**
     * Current [State] of the store
     */
    val currentState: S

    /**
     * Dispatches an [Intent]
     */
    fun dispatch(intent: I)

    /**
     * Processes an [Event]
     */
    fun process(event: A)

    /**
     * Cancels all jobs within the store
     */
    fun dispose()

    /**
     * Collect used for Kotlin Native
     */
    fun collect(
        onState: (S) -> Unit,
        onEvent: (A?) -> Unit,
    ): Job
}
