import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//fun Project.setupKotlinCompile() {
//    tasks.withType<KotlinCompile> {
//        targetCompatibility = Config.javaVersion.toString()
//        kotlinOptions {
//            jvmTarget = Config.javaVersion.toString()
//            freeCompilerArgs = listOf(
//                "-Xskip-prerelease-check",
//                "-P",
//            )
//        }
//    }
//}