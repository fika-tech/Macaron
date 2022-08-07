setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":macaron-core"))
                implementation(Dependencies.kotlinx.coroutinesCore)
                implementation(Dependencies.logger.kermit)
            }
        }
    }
}
