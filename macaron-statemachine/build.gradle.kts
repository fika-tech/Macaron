setupMultiplatform()

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
