import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

fun Project.setupMultiplatform(
    vararg buildTargets: BuildTarget = arrayOf(BuildTarget.Ios, BuildTarget.Android),
    additionalPlugins: List<Provider<PluginDependency>> = listOf(),
    publish: Boolean = true,
) {
    plugins.apply(Dependencies.plugins.kotlinMultiplatform)

    setupLinter()
    if (publish) {
        setupMavenPublication()
    }

    setup<BuildTarget.Android>(buildTargets) {
        plugins.apply(Dependencies.plugins.androidLibrary)
        setupAndroid()
    }

    additionalPlugins.forEach { plugins.apply(it) }

    kotlin {
        setup<BuildTarget.Js>(buildTargets) {
            js(IR) {
                useCommonJs()
                browser()
            }
        }

        setup<BuildTarget.Android>(buildTargets) {
            android {
                publishAllLibraryVariants()
            }
        }

        setup<BuildTarget.Jvm>(buildTargets) {
            jvm()
        }

        setup<BuildTarget.Ios>(buildTargets) {
            ios()
            // TODO: Setup iOS Simulator
            iosSimulatorArm64()
        }

        setup<BuildTarget.MacOsX64>(buildTargets) {
            macosX64()
        }

        sourceSets {
            setup<BuildTarget.Ios>(buildTargets) {
                iosSimulatorArm64Main.dependsOn(iosMain)
                iosSimulatorArm64Test.dependsOn(iosTest)
            }
            all {
                languageSettings.apply {
                    optIn("kotlin.RequiresOptIn")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }
        }
    }
}
