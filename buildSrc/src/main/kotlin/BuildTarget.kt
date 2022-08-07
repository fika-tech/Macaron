interface BuildTarget {
    interface NonNative : BuildTarget
    interface Native : BuildTarget
    interface Darwin : Native
    interface Linux : Native
    object Android : NonNative
    object Jvm : NonNative
    object Js : NonNative
    object Ios : Darwin
    object MacOsX64 : Darwin
    object LinuxX64 : Linux
}

internal inline fun <reified T : BuildTarget> setup(buildTargets: Array<out BuildTarget>, block: () -> Unit) {
    if (buildTargets.any { it is T }) {
        block()
    }
}
