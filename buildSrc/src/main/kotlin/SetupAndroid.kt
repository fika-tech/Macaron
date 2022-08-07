import org.gradle.api.Project

fun Project.setupAndroid() {
    android {
        compileSdkVersion(Config.Android.compileSdk)

        defaultConfig {
            targetSdk = Config.Android.targetSdk
            minSdk = Config.Android.minSdk
            testInstrumentationRunner = Config.Android.instrumentedTestRunner
        }

        buildTypes {
            getByName(Config.release) {
                isMinifyEnabled = false
            }
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
        }

        compileOptions {
            sourceCompatibility = Config.javaVersion
            targetCompatibility = Config.javaVersion
        }

        packagingOptions {
            resources.excludes.run {
                remove("**/*.kotlin_metadata")
                add("META-INF/AL2.0")
                add("META-INF/LGPL2.1")
                add("META-INF/licenses/ASM")
                add("win32-x86/**")
                add("win32-x86-64/**")
            }
        }
    }
}
