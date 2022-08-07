package tech.fika.macaron.statesaver

import tech.fika.macaron.core.contract.State

interface StateSaver<S : State> {
    suspend fun save(state: S)
    fun get(): S?
    fun hasState(): Boolean
    fun onInit(block: () -> Unit) = if (hasState()) block() else Unit
}