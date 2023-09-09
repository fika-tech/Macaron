setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "messaging"))

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
