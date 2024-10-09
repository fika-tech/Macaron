package tech.fika.macaron.android.sample

import com.arkivanov.essenty.lifecycle.Lifecycle
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

sealed class SampleAction : Action {
    data object Start : SampleAction()
    data object Pause : SampleAction()
    data object Stop : SampleAction()
    internal data object OnResume : SampleAction()
    internal data object OnPause : SampleAction()
    internal data object Decrement : SampleAction()
    internal data object Complete : SampleAction()
}

sealed class SampleEvent : Event {
    data object Complete : SampleEvent()
    data object Error : SampleEvent()
}

sealed class SampleState : State {
    data object Stopped : SampleState()
    data class Started(val counter: Int) : SampleState()
    data class Paused(val counter: Int) : SampleState()
}
