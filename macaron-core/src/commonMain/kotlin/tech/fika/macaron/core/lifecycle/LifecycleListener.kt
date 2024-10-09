package tech.fika.macaron.core.lifecycle

import com.arkivanov.essenty.lifecycle.Lifecycle
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.State

interface LifecycleListener<A : Action, S : State> {
    fun onCreate(state: S, dispatch: (A) -> Unit) = Unit
    fun onStart(state: S, dispatch: (A) -> Unit) = Unit
    fun onResume(state: S, dispatch: (A) -> Unit) = Unit
    fun onPause(state: S, dispatch: (A) -> Unit) = Unit
    fun onStop(state: S, dispatch: (A) -> Unit) = Unit
    fun onDestroy() = Unit

    fun toLifecycleCallbacks(getState: () -> S, dispatch: (A) -> Unit): Lifecycle.Callbacks = object : Lifecycle.Callbacks {
        override fun onCreate() = onCreate(getState(), dispatch)
        override fun onStart() = onStart(getState(), dispatch)
        override fun onResume() = onResume(getState(), dispatch)
        override fun onPause() = onPause(getState(), dispatch)
        override fun onStop() = onStop(getState(), dispatch)
        override fun onDestroy() { onDestroy() }
    }

    companion object {
        fun <A : Action, S : State> default() = object : LifecycleListener<A, S> {}
    }
}
