import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.implementations(vararg dependencies: Provider<MinimalExternalModuleDependency>) = dependencies.forEach(::implementation)

fun KotlinDependencyHandler.apis(vararg dependencies: Provider<MinimalExternalModuleDependency>) = dependencies.forEach(::api)
