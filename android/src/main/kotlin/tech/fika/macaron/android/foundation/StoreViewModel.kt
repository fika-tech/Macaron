package tech.fika.macaron.android.foundation

import androidx.lifecycle.ViewModel
import tech.fika.macaron.core.store.Store
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

abstract class StoreViewModel<A : Action, E : Event, S : State> : ViewModel() {

    abstract val store: Store<A, E, S>

    override fun onCleared() {
        super.onCleared()
        store.dispose()
    }
}
