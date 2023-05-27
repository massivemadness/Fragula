import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("binary-compatibility-validator")
}

Gradle.Fragula.libraryGroupId = "com.fragula2"
Gradle.Fragula.libraryArtifactId = "fragula-compose"

apply(from = "../gradle/publish.gradle")

android {
    compileSdk = Gradle.Fragula.compileSdk
    buildToolsVersion = Gradle.Fragula.buildTools

    group = Gradle.Fragula.libraryGroupId
    version = Gradle.Fragula.libraryVersionName
    namespace = "com.fragula2.compose"

    defaultConfig {
        minSdk = Gradle.Fragula.minSdk
        targetSdk = Gradle.Fragula.targetSdk

        consumerProguardFiles("consumer-rules.pro")

        base.archivesName.set(Gradle.Fragula.libraryArtifactId)
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