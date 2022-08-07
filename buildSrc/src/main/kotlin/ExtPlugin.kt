import org.gradle.api.Plugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

fun PluginContainer.apply(plugin: Provider<PluginDependency>): Plugin<Any> = apply(plugin.get().pluginId)
