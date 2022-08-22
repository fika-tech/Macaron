rootProject.name = "macaron"

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("config/deps.versions.toml"))
        }
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":macaron-core")
include(":macaron-statemachine")
include(":macaron-logging")
include(":macaron-messaging")
include(":macaron-statesaver")
include(":macaron-timemachine")
