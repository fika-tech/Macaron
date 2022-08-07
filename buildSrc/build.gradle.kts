plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    // ref: https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(deps.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(deps.gradle.kotlin)
    implementation(deps.gradle.android)
    implementation(deps.gradle.kover)
    implementation(deps.gradle.ktlint)
    implementation(deps.gradle.dokka)
}
