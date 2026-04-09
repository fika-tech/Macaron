import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun Project.setupKotlinCompile() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(Config.javaVersion.toString()))
            freeCompilerArgs.addAll(
                "-Xskip-prerelease-check",
                "-P",
            )
        }
    }
}
