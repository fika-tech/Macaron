package tech.fika.macaron.timemachine

import tech.fika.macaron.core.contract.Contract
import tech.fika.macaron.core.contract.State

data class TimeMachineEntry(
    val store: String,
    val value: Contract,
    val currentState: State,
)