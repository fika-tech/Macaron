setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "statesaver"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":macaron-core"))
                implementation(Dependencies.kotlinx.coroutinesCore)
            }
        }
    }
}
