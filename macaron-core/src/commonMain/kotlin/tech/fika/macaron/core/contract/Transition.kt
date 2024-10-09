package tech.fika.macaron.core.contract

sealed interface Transition<out A : Action, out S : State, out S1 : State> {
    data object Empty : Transition<Nothing, Nothing, Nothing>

    data class Valid<out A : Action, out S : State, out S1 : State>(val action: A, val currentState: S, val nextState: S1) : Transition<A, S, S1>

    data object Invalid : Transition<Nothing, Nothing, Nothing>
}
