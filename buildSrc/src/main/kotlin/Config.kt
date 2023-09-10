import org.gradle.api.JavaVersion

object Config {
    const val group = "tech.fika.macaron"
    const val version = "0.1.0"
    val javaVersion = JavaVersion.VERSION_17
    const val release = "release"
    const val debug = "debug"

    object Android {
        const val minSdk = 24
        const val targetSdk = 32
        const val compileSdk = targetSdk
        const val instrumentedTestRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    internal object Publication {
        const val mavenName = "sonatype"
        const val name = "Macaron"
        const val repository = "fika/$name"
        const val baseUrl = "github.com/$repository"
        const val issueSystem = "Github"
        const val issuesUrl = "https://${baseUrl}/issues"
        const val connectionUrl = "scm:git:git://${baseUrl}.git"
        const val license = "MIT"
        const val licenseUrl = "https://opensource.org/licenses/MIT"
        val developers = listOf(
            Developer(name = "Marco Valentino", email = "gmvalentino@fika.tech")
        )
        const val properties = "publishing.properties"
        private const val releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        private const val snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        fun url(version: String) = if (version.endsWith("SNAPSHOT")) snapshotUrl else releaseUrl
    }

    internal data class Developer(val name: String, val email: String)
}
