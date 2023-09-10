setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "statemachine"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":macaron-core"))
                implementations(
                    Dependencies.kotlinx.coroutinesCore,
                )
            }
        }
    }
}
