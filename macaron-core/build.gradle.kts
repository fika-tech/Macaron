setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Dependencies.kotlinx.coroutinesCore)
            }
        }
    }
}
