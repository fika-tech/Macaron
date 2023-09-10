package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

/**
 * [Reducer] receives [Action]s from the [Processor] and updates the [State]
 */
interface Reducer<A : Action, S : State> {
    /**
     * A pure function that applies a [Action] to the current [State] and returns a new [State]
     */
    suspend fun reduce(action: A, state: S): S
}
