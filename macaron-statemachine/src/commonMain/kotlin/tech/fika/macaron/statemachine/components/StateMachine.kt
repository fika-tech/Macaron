package tech.fika.macaron.statemachine.components

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import tech.fika.macaron.core.components.Interpreter
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Contract
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State

open class StateMachine<I : Intent, A : Action, R : Result, S : State, E : Event>(
    val graph: Map<Matcher<S, S>, StateNode<I, A, R, S, E>>,
) {
    constructor(builder: StateMachineBuilder<I, A, R, S, E>.() -> Unit) :
        this(StateMachineBuilder<I, A, R, S, E>().apply(builder).build().graph)

    val interpreter: Interpreter<I, A, S> get() = StateMachineInterpreter(this)
    val processor: Processor<A, R, S, E> get() = StateMachineProcessor(this)
    val reducer: Reducer<R, S> get() = StateMachineReducer(this)

    class Matcher<T : Contract, out R : T> private constructor(private val kClass: KClass<R>) {
        private val predicates = mutableListOf<(T) -> Boolean>({ kClass.isInstance(it) })

        fun matches(value: T) = predicates.all { it(value) }

        companion object {
            fun <T : Contract, R : T> any(kClass: KClass<R>): Matcher<T, R> = Matcher(kClass)

            inline fun <T : Contract, reified R : T> any(): Matcher<T, R> = any(R::class)
        }
    }

    class StateNode<I : Intent, A : Action, R : Result, S : State, E : Event> {
        val intents = mutableMapOf<Matcher<I, I>, (S, I) -> A?>()
        val actions = mutableMapOf<Matcher<A, A>, suspend (S, A) -> Flow<SideEffect<R, E>>>()
        val results = mutableMapOf<Matcher<R, R>, (S, R) -> S?>()
    }

    sealed class SideEffect<R : Result, E : Event> {
        data class ResultNode<R : Result, E : Event>(val value: R) : SideEffect<R, E>()
        data class EventNode<R : Result, E : Event>(val value: E) : SideEffect<R, E>()
    }

    class StateMachineBuilder<I : Intent, A : Action, R : Result, S : State, E : Event> {
        private val graph = LinkedHashMap<Matcher<S, S>, StateNode<I, A, R, S, E>>()

        fun <STATE : S> state(stateMatcher: Matcher<S, STATE>, config: StateNodeBuilder<STATE>.() -> Unit) {
            graph[stateMatcher] = StateNodeBuilder<STATE>().apply(config).build()
        }

        inline fun <reified STATE : S> state(noinline config: StateNodeBuilder<STATE>.() -> Unit) = state(Matcher.any(), config)

        fun build(): StateMachine<I, A, R, S, E> = StateMachine(graph.toMap())

        @Suppress("UNCHECKED_CAST")
        inner class StateNodeBuilder<STATE : S> {
            private val stateNode = StateNode<I, A, R, S, E>()

            fun <INTENT : I> interpret(intentMatcher: Matcher<I, INTENT>, interpret: STATE.(INTENT) -> A?) {
                stateNode.intents[intentMatcher] = { state, intent -> interpret(state as STATE, intent as INTENT) }
            }

            inline fun <reified INTENT : I> interpret(noinline interpret: STATE.(INTENT) -> A?) = interpret(Matcher.any(), interpret)

            fun <ACTION : A> process(actionMatcher: Matcher<A, ACTION>, process: suspend STATE.(ACTION) -> Flow<SideEffect<R, E>>) {
                stateNode.actions[actionMatcher] = { state, action -> process(state as STATE, action as ACTION) }
            }

            inline fun <reified ACTION : A> process(noinline process: suspend STATE.(ACTION) -> Flow<SideEffect<R, E>>) = process(Matcher.any(), process)

            fun <RESULT : R> reduce(resultMatcher: Matcher<R, RESULT>, reduce: STATE.(RESULT) -> S?) {
                stateNode.results[resultMatcher] = { state, result -> reduce(state as STATE, result as RESULT) }
            }

            inline fun <reified RESULT : R> reduce(noinline reduce: STATE.(RESULT) -> S?) = reduce(Matcher.any(), reduce)

            fun build() = stateNode
        }
    }
}
