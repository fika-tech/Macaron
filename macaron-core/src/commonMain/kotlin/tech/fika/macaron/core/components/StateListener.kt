package tech.fika.macaron.core.components

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

interface StateListener<A : Action, S : State> {
    fun onEnter(state: S, dispatch: (A) -> Unit) = Unit
    fun onRepeat(state: S, dispatch: (A) -> Unit) = Unit
    fun onExit(state: S, dispatch: (A) -> Unit) = Unit

    companion object {
        fun <A : Action, S : State> default() = object : StateListener<A, S> {}
    }
}
