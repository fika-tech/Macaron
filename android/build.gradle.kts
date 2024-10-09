plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
//    alias(deps.plugins.hilt)
//    alias(deps.plugins.ksp)
}

kotlin.apply {
    jvmToolchain(17)
}

androidApp {
    namespace = "tech.fika.macaron.android"
    compileSdk = 34

    defaultConfig {
        targetSdk = 34
        minSdk = 24
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":macaron-core"))
    implementation(project(":macaron-logging"))
    implementation(project(":macaron-statemachine"))
//    implementation(project(":macaron-statemachine-erased"))
    platform(deps.kotlin.bom)
    platform(deps.compose.bom)
    implementation(Dependencies.essenty.lifecycle)
    implementation(deps.androidx.navigation.runtime.ktx)
    implementation(deps.androidx.navigation.compose)
    debugImplementation(deps.androidx.ui.tooling)
    implementation(deps.compose.ui)
    implementation(deps.compose.ui.graphics)
    implementation(deps.compose.ui.tooling.preview)
    implementation(deps.compose.material3)
    implementation(deps.androidx.activity.compose)
    implementation(deps.androidx.lifecycle.runtime.compose)
    implementation(deps.logger.kermit)
//    implementation(deps.hilt.android)
//    ksp(deps.hilt.compiler)
}
