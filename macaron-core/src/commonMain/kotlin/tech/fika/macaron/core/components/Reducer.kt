package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

/**
 * [Reducer] receives [Result]s from the [Processor] and creates a new [State]
 */
interface Reducer<in R : Result, S : State> {
    /**
     * A pure function that applies a [Result] to the current [State] and returns a new [State]
     */
    suspend fun reduce(result: R, state: S): S?
}
