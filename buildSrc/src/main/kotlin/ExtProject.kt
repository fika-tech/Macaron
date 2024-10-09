import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.accessors.dm.LibrariesForDeps
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

val Project.Dependencies: LibrariesForDeps get() = extensions.getByType(LibrariesForDeps::class.java)

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) = extensions.getByType<KotlinMultiplatformExtension>().block()

fun Project.android(block: BaseExtension.() -> Unit) = extensions.getByType<BaseExtension>().block()

fun Project.androidApp(block: BaseAppModuleExtension.() -> Unit) = extensions.configure(block)

fun Project.ktlint(block: KtlintExtension.() -> Unit) = extensions.getByType(KtlintExtension::class.java).block()

fun Project.publications(block: PublishingExtension.() -> Unit) = extensions.getByType<PublishingExtension>().block()
