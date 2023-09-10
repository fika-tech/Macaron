setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "core"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Dependencies.kotlinx.coroutinesCore)
            }
        }
    }
}
