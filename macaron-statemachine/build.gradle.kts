setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "statemachine"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":macaron-core"))
                implementation(Dependencies.essenty.lifecycle)
                implementations(
                    Dependencies.kotlinx.coroutinesCore,
                )
            }
        }
    }
}
