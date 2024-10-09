package tech.fika.macaron.core.store

import tech.fika.macaron.core.components.Interceptor
import tech.fika.macaron.core.lifecycle.LifecycleListener
import tech.fika.macaron.core.components.StateListener
import tech.fika.macaron.core.contract.Action
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.State

data class StoreConfiguration<A : Action, E : Event, S : State>(
    val stateListener: StateListener<A, S>? = null,
    val lifecycleListener: LifecycleListener<A, S>? = null,
    val interceptors: List<Interceptor<A, E, S>> = emptyList(),
) {
    class Builder<A : Action, E : Event, S : State> {
        private var stateListener: StateListener<A, S>? = null
        private var lifecycleListener: LifecycleListener<A, S>? = null
        private var interceptors: MutableList<Interceptor<A, E, S>> = mutableListOf()

        fun add(stateListener: StateListener<A, S>?) {
            this.stateListener = stateListener
        }

        fun add(lifecycleListener: LifecycleListener<A, S>?) {
            this.lifecycleListener = lifecycleListener
        }

        fun add(interceptor: Interceptor<A, E, S>?) {
            if (interceptor != null) {
                this.interceptors.add(interceptor)
            }
        }

        fun add(interceptors: List<Interceptor<A, E, S>>) {
            this.interceptors.addAll(interceptors)
        }

        fun build(): StoreConfiguration<A, E, S> = StoreConfiguration(
            stateListener = stateListener,
            lifecycleListener = lifecycleListener,
            interceptors = interceptors
        )
    }
}