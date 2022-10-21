package tech.fika.macaron.statemachine.components

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import tech.fika.macaron.core.components.Interpreter
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Contract
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.factory.StoreFactory
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

fun <I : Intent, A : Action, R : Result, S : State, E : Event> StoreFactory.create(
    initialState: S,
    stateMachine: StateMachine<I, A, R, S, E>,
    middlewares: List<Middleware<I, A, R, S, E>> = emptyList(),
    coroutineContext: CoroutineContext = Dispatchers.Main,
) = create(
    initialState = initialState,
    interpreter = stateMachine.interpreter,
    processor = stateMachine.processor,
    reducer = stateMachine.reducer,
    middlewares = middlewares,
    coroutineContext = coroutineContext
)

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

    data class InterpreterNode<S : State, I : Intent>(val state: S, val intent: I)
    data class ProcessorNode<S : State, A : Action>(val state: S, val action: A)
    data class ReducerNode<S : State, R : Result>(val state: S, val result: R)

    class StateNode<I : Intent, A : Action, R : Result, S : State, E : Event> {
        val intents = mutableMapOf<Matcher<I, I>, InterpreterNode<S, I>.() -> A?>()
        val actions = mutableMapOf<Matcher<A, A>, suspend (ProcessorNode<S, A>) -> Flow<SideEffect<R, E>>>()
        val results = mutableMapOf<Matcher<R, R>, ReducerNode<S, R>.() -> S?>()
    }

    sealed class SideEffect<R : Result, E : Event> {
        data class ResultNode<R : Result, E : Event>(val value: R) : SideEffect<R, E>()
        data class EventNode<R : Result, E : Event>(val value: E) : SideEffect<R, E>()
    }

    class StateMachineBuilder<I : Intent, A : Action, R : Result, S : State, E : Event> {
        internal val graph = LinkedHashMap<Matcher<S, S>, StateNode<I, A, R, S, E>>()

        fun <STATE : S> state(stateMatcher: Matcher<S, STATE>, config: StateNodeBuilder<STATE>.() -> Unit) {
            graph[stateMatcher] = StateNodeBuilder<STATE>().apply(config).build()
        }

        inline fun <reified STATE : S> state(noinline config: StateNodeBuilder<STATE>.() -> Unit) = state(Matcher.any(), config)

        fun build(): StateMachine<I, A, R, S, E> = StateMachine(graph.toMap())

        @Suppress("UNCHECKED_CAST")
        inner class StateNodeBuilder<STATE : S> {
            private val stateNode = StateNode<I, A, R, S, E>()

            fun <INTENT : I> interpret(intentMatcher: Matcher<I, INTENT>, interpret: InterpreterNode<STATE, INTENT>.() -> A?) {
                stateNode.intents[intentMatcher] = { interpret(this as InterpreterNode<STATE, INTENT>) }
            }

            inline fun <reified INTENT : I> interpret(noinline interpret: InterpreterNode<STATE, INTENT>.() -> A?) = interpret(Matcher.any(), interpret)

            fun <ACTION : A> process(
                actionMatcher: Matcher<A, ACTION>,
                process: suspend FlowCollector<SideEffect<R, E>>.(ProcessorNode<STATE, ACTION>) -> Unit,
            ) {
                stateNode.actions[actionMatcher] = { node ->
                    flow { process(this, node as ProcessorNode<STATE, ACTION>) }
                }
            }

            inline fun <reified ACTION : A> process(noinline process: suspend FlowCollector<SideEffect<R, E>>.(ProcessorNode<STATE, ACTION>) -> Unit) =
                process(Matcher.any(), process)

            fun <RESULT : R> reduce(resultMatcher: Matcher<R, RESULT>, reduce: ReducerNode<STATE, RESULT>.() -> S?) {
                stateNode.results[resultMatcher] = { reduce(this as ReducerNode<STATE, RESULT>) }
            }

            inline fun <reified RESULT : R> reduce(noinline reduce: ReducerNode<STATE, RESULT>.() -> S?) = reduce(Matcher.any(), reduce)

            fun build() = stateNode
        }
    }
}

suspend fun <R : Result, E : Event> FlowCollector<StateMachine.SideEffect<R, E>>.result(result: R) = emit(StateMachine.SideEffect.ResultNode(result))

suspend fun <R : Result, E : Event> FlowCollector<StateMachine.SideEffect<R, E>>.event(event: E) = emit(StateMachine.SideEffect.EventNode(event))
