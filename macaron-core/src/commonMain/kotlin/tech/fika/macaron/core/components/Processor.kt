package tech.fika.macaron.core.components

import kotlinx.coroutines.flow.Flow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

/**
 * Processors receives [Action] from the [Interpreter] and processes it
 */
interface Processor<A : Action, R : Result, S : State, E : Event> {
    /**
     * Execute an [Action] given a current [State] and returns a stream of [Result]
     *
     * Side-effects should be processed here (i.e. Usecases, Repository)
     */
    suspend fun process(action: A, state: S, send: (E) -> Unit): Flow<R>
}
