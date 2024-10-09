setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "messaging"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":macaron-core"))
                implementation(project(":macaron-statemachine"))
                implementation(Dependencies.essenty.lifecycle)
                implementation(Dependencies.kotlinx.coroutinesCore)
            }
        }
    }
}
