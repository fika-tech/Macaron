package tech.fika.macaron.statemachine.components

import tech.fika.macaron.core.components.Interpreter
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

class StateMachineInterpreter<I : Intent, A : Action, R : Result, S : State, E : Event>(
    private val stateMachine: StateMachine<I, A, R, S, E>,
) : Interpreter<I, A, S> {
    override suspend fun interpret(intent: I, state: S): A? = stateMachine.graph
        .filterKeys { key -> key.matches(state) }.values
        .flatMap { stateNode -> stateNode.intents.entries }
        .find { intentMatcher -> intentMatcher.key.matches(intent) }
        ?.value
        ?.invoke(StateMachine.InterpreterNode(state, intent))
}
