import java.io.FileInputStream
import java.util.Properties

enum class Property(val key: String) {
    Version("VERSION"),
    SonatypeUsername("SONATYPE_USERNAME"),
    SonatypePassword("SONATYPE_PASSWORD"),
    GpgKeyId("GPG_KEY_ID"),
    GpgKey("GPG_KEY"),
    GpgPassword("GPG_PASSWORD")
    ;

    companion object {
        fun get(property: Property): String? = System.getProperty(property.key) ?: System.getenv(property.key)
        fun get(property: Property, file: String): String? = Properties().apply {
            runCatching {
                load(FileInputStream(file))
            }
        }.getProperty(property.key) ?: get(property)
    }
}
