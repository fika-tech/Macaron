package tech.fika.macaron.core.components

import kotlinx.coroutines.flow.Flow
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

/**
 * Interpreters receive [Intent] from user input and processed each it to flow of [Action]
 */
interface Processor<I : Intent, A : Action, S : State> {
    /**
     * Execute an [Intent] given a current [State] and returns a stream of [Action]
     *
     * Side-effects should be processed here (i.e. UseCases, Repository)
     */
    suspend fun process(intent: I, state: S): Flow<A>
}
