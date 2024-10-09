package tech.fika.macaron.statemachine.erased

import tech.fika.macaron.statemachine.components.LifecycleDsl

data class LifecycleErasedNode(
    val onCreate: () -> Unit = {},
    val onStart: () -> Unit = {},
    val onResume: () -> Unit = {},
    val onPause: () -> Unit = {},
    val onStop: () -> Unit = {},
    val onDestroy: () -> Unit = {},
)

@LifecycleDsl
class LifecycleErasedBuilder {
    private var onCreate: () -> Unit = {}
    private var onStart: () -> Unit = {}
    private var onResume: () -> Unit = {}
    private var onPause: () -> Unit = {}
    private var onStop: () -> Unit = {}
    private var onDestroy: () -> Unit = {}

    fun onCreate(block: () -> Unit) {
        onCreate = block
    }

    fun onStart(block: () -> Unit) {
        onStart = block
    }

    fun onResume(block: () -> Unit) {
        onResume = block
    }

    fun onPause(block: () -> Unit) {
        onPause = block
    }

    fun onStop(block: () -> Unit) {
        onStop = block
    }

    fun onDestroy(block: () -> Unit) {
        onDestroy = block
    }

    fun build() = LifecycleErasedNode(
        onCreate = onCreate,
        onStart = onStart,
        onResume = onResume,
        onPause = onPause,
        onStop = onStop,
        onDestroy = onDestroy
    )
}