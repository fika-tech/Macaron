package tech.fika.macaron.statemachine.components

import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import tech.fika.macaron.core.components.Middleware
import tech.fika.macaron.core.components.Processor
import tech.fika.macaron.core.components.Reducer
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Contract
import tech.fika.macaron.core.contract.Intent
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.factory.StoreFactory

fun <I : Intent, A : Action, S : State> StoreFactory.create(
    initialState: S,
    stateMachine: StateMachine<I, A, S>,
    middlewares: List<Middleware<I, A, S>> = emptyList(),
    coroutineContext: CoroutineContext = Dispatchers.Main,
) = create(
    initialState = initialState,
    processor = stateMachine.processor,
    reducer = stateMachine.reducer,
    middlewares = middlewares,
    coroutineContext = coroutineContext,
)

@Suppress("UNCHECKED_CAST")
open class StateMachine<I : Intent, A : Action, S : State>(
    val stateMap: Map<Matcher<S, S>, StateNode<I, A, S>>,
) {
    constructor(builder: Builder<I, A, S>.() -> Unit) : this(Builder<I, A, S>().apply(builder).build().stateMap)

    val processor: Processor<I, A, S> get() = StateMachineProcessor(this)
    val reducer: Reducer<A, S> get() = StateMachineReducer(this)

    class Matcher<T : Contract, out R : T> private constructor(private val kClass: KClass<R>) {

        private val predicates = mutableListOf<(T) -> Boolean>({ kClass.isInstance(it) })
        fun matches(value: T) = predicates.all { it(value) }

        companion object {
            fun <T : Contract, R : T> any(kClass: KClass<R>): Matcher<T, R> = Matcher(kClass)
            inline fun <T : Contract, reified R : T> any(): Matcher<T, R> = any(R::class)
        }
    }

    /* Nodes */

    data class IntentNode<I : Intent, S : State>(val intent: I, val state: S)

    data class ActionNode<A : Action, S : State>(val action: A, val state: S)

    data class StateNode<I : Intent, A : Action, S : State>(
        val intentMap: Map<Matcher<I, I>, suspend (IntentNode<I, S>) -> Flow<A>>,
        val actionMap: Map<Matcher<A, A>, (ActionNode<A, S>) -> S?>,
    )

    /* Scopes */

    data class IntentScope<I : Intent, A : Action, S : State>(
        val intent: I,
        val state: S,
        internal val collector: FlowCollector<A>,
    ) {
        suspend fun emit(action: A) = collector.emit(action)
        suspend operator fun A.unaryPlus() = emit(this)
    }

    data class ActionScope<A : Action, S : State>(val action: A, val state: S)

    /* Annotations */

    @DslMarker
    @Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
    private annotation class StateMachineDsl

    @DslMarker
    @Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
    private annotation class StateDsl

    @StateMachineDsl
    class Builder<I : Intent, A : Action, S : State> {
        private val stateMap = LinkedHashMap<Matcher<S, S>, StateNode<I, A, S>>()

        fun <STATE : S> state(stateMatcher: Matcher<S, STATE>, builder: @StateMachineDsl StateBuilder<STATE>.() -> Unit) {
            stateMap[stateMatcher] = StateBuilder<STATE>().apply(builder).build()
        }

        inline fun <reified STATE : S> state(noinline builder: @StateMachineDsl StateBuilder<STATE>.() -> Unit) = state(Matcher.any(), builder)

        fun build(): StateMachine<I, A, S> = StateMachine(stateMap.toMap())

        @StateDsl
        inner class StateBuilder<STATE : S> {
            private val intentMap = mutableMapOf<Matcher<I, I>, suspend (IntentNode<I, S>) -> Flow<A>>()
            private val actionMap = mutableMapOf<Matcher<A, A>, (ActionNode<A, S>) -> S?>()

            fun <INTENT : I> process(
                intentMatcher: Matcher<I, INTENT>,
                intent: @StateDsl suspend IntentScope<INTENT, A, STATE>.() -> Unit,
            ) {
                intentMap[intentMatcher] = { (intent, state) ->
                    flow {
                        intent(IntentScope(intent = intent, state = state, collector = this) as IntentScope<INTENT, A, STATE>)
                    }
                }
            }

            inline fun <reified INTENT : I> process(noinline intent: @StateDsl suspend IntentScope<INTENT, A, STATE>.() -> Unit) =
                process(Matcher.any(), intent)

            fun <ACTION : A> reduce(
                actionMatcher: Matcher<A, ACTION>,
                reduce: @StateDsl ActionScope<ACTION, STATE>.() -> S,
            ) {
                actionMap[actionMatcher] = { (action, state) ->
                    reduce(ActionScope(action, state) as ActionScope<ACTION, STATE>)
                }
            }

            inline fun <reified ACTION : A> reduce(noinline action: @StateDsl ActionScope<ACTION, STATE>.() -> S) = reduce(Matcher.any(), action)

            fun build(): StateNode<I, A, S> = StateNode(intentMap = intentMap, actionMap = actionMap)
        }
    }
}
