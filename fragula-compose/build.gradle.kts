import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("binary-compatibility-validator")
}

Gradle.Maven.libraryGroupId = "com.fragula2"
Gradle.Maven.libraryArtifactId = "fragula-compose"

apply(from = "../gradle/publish.gradle")

android {
    compileSdk = Gradle.Build.compileSdk
    buildToolsVersion = Gradle.Build.buildTools

    group = Gradle.Maven.libraryGroupId
    version = Gradle.Maven.libraryVersionName
    namespace = "com.fragula2.compose"

    defaultConfig {
        minSdk = Gradle.Build.minSdk
        targetSdk = Gradle.Build.targetSdk

        consumerProguardFiles("consumer-rules.pro")

        base.archivesName.set(Gradle.Maven.libraryArtifactId)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    sourceSets {
        named("main") {
            java.srcDir("src/main/kotlin")
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.navigation)

    // Common
    api(project(":fragula-common"))
}