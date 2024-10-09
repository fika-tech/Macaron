package tech.fika.macaron.statemachine.erased

import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State
import tech.fika.macaron.core.lifecycle.LifecycleAction
import tech.fika.macaron.core.lifecycle.LifecycleListener
import tech.fika.macaron.statemachine.components.StateMachine

class StateMachineLifecycleListenerErased<S : State>(
    private val stateMachine: StateMachineErased,
) : LifecycleListener<Action, S> {
    override fun onCreate(state: S, dispatch: (Action) -> Unit) {
        dispatch(LifecycleAction.OnCreate)
        stateMachine.lifecycleNode.onCreate()
    }

    override fun onStart(state: S, dispatch: (Action) -> Unit) {
        dispatch(LifecycleAction.OnStart)
        stateMachine.lifecycleNode.onStart()
    }

    override fun onResume(state: S, dispatch: (Action) -> Unit) {
        dispatch(LifecycleAction.OnResume)
        stateMachine.lifecycleNode.onResume()
    }

    override fun onPause(state: S, dispatch: (Action) -> Unit) {
        dispatch(LifecycleAction.OnPause)
        stateMachine.lifecycleNode.onPause()
    }

    override fun onStop(state: S, dispatch: (Action) -> Unit) {
        dispatch(LifecycleAction.OnStop)
        stateMachine.lifecycleNode.onStop()
    }

    override fun onDestroy() = stateMachine.lifecycleNode.onDestroy()
}