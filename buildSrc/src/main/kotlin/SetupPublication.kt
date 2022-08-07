import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

fun Project.setupMavenPublication() {
    plugins.apply("maven-publish")
    plugins.apply("signing")
    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        val publishing = extensions.getByType<PublishingExtension>().apply {
            publications {
                repositories {
                    maven("${rootProject.rootDir}/artifacts/maven")
                    maven {
                        name = Config.Publication.mavenName
                        url = uri(Config.Publication.url(version.toString()))
                        credentials {
                            username = Property.get(Property.SonatypeUsername, Config.Publication.properties)
                            password = Property.get(Property.SonatypePassword, Config.Publication.properties)
                        }
                    }
                }
                withType<MavenPublication> {
                    artifact(javadocJar)
                    pom {
                        name.set(Config.Publication.name)
                        description.set("Kotlin Multiplatform MVI Framework")
                        licenses {
                            license {
                                name.set(Config.Publication.license)
                                url.set(Config.Publication.licenseUrl)
                            }
                        }
                        url.set(Config.Publication.baseUrl)
                        issueManagement {
                            system.set(Config.Publication.issueSystem)
                            url.set(Config.Publication.issuesUrl)
                        }
                        scm {
                            connection.set(Config.Publication.connectionUrl)
                            url.set(Config.Publication.baseUrl)
                        }
                        developers {
                            Config.Publication.developers.forEach { dev ->
                                developer {
                                    name.set(dev.name)
                                    email.set(dev.email)
                                }
                            }
                        }
                    }
                }
            }
        }

        extensions.getByType<SigningExtension>().run {
            useInMemoryPgpKeys(
//                Property.get(Property.GpgKeyId, Config.Publication.properties),
                Property.get(Property.GpgKey, Config.Publication.properties),
                Property.get(Property.GpgPassword, Config.Publication.properties)
            )
            sign(publishing.publications)
        }
    }
}
