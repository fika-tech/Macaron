package tech.fika.macaron.timemachine

data class TimeMachineState(
    val events: List<TimeMachineEntry> = emptyList(),
    val selectedIndex: Int = -1,
    val mode: Mode = Mode.Idle,
) {

    enum class Mode {
        Idle, Recording, Stopped
    }
}