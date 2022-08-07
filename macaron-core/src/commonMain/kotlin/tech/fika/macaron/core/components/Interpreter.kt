package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

/**
 * Interpreters receive [Intent] from user input and maps it to an [Action]
 */
interface Interpreter<in I : Intent, out A : Action, S : State> {
    suspend fun interpret(intent: I, state: S): A?
}
