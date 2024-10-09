import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

fun Project.setupMultiplatform(
    vararg buildTargets: BuildTarget,
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
        setupAndroid(namespace = namespace)
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
            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }

        setup<BuildTarget.MacOsX64>(buildTargets) {
            macosX64()
        }

        sourceSets {
            all {
                languageSettings.apply {
                    optIn("kotlin.RequiresOptIn")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }
        }
    }
}
