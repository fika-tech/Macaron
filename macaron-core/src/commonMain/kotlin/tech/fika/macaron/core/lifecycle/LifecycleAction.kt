package tech.fika.macaron.core.lifecycle

import tech.fika.macaron.core.contract.Action

sealed interface LifecycleAction : Action {
    data object OnCreate : LifecycleAction
    data object OnStart : LifecycleAction
    data object OnResume : LifecycleAction
    data object OnPause : LifecycleAction
    data object OnStop : LifecycleAction
    data object OnDestroy : LifecycleAction
}