setupMultiplatform(BuildTarget.Ios, BuildTarget.Android(namespace = "core"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Dependencies.kotlinx.coroutinesCore)
                implementation(Dependencies.logger.kermit)
                implementation(Dependencies.essenty.lifecycle)
            }
        }
    }
}
