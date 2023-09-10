package tech.fika.macaron.statemachine.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State

class StateMachineProcessor<I : Intent, A : Action, S : State>(
    private val stateMachine: StateMachine<I, A, S>,
) : Processor<I, A, S> {
    override suspend fun process(intent: I, state: S): Flow<A> = stateMachine.stateMap
        .filterKeys { key -> key.matches(state) }
        .values
        .flatMap { stateNode -> stateNode.intentMap.entries }
        .find { intentMatcher -> intentMatcher.key.matches(intent) }
        ?.value
        ?.invoke(StateMachine.IntentNode(state = state, intent = intent))
        ?.filterNotNull()
        ?: flowOf()
}
