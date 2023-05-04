//import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
//    id("kotlin-android")
    kotlin("android")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
//    id("binary-compatibility-validator")
}

//Gradle.Maven.libraryGroupId = "com.fragula2"
//Gradle.Maven.libraryArtifactId = "fragula-compose"
//
//apply(from = "../gradle/publish.gradle")

android {
    compileSdk = 33

//    group = ""
//    version = "2.6"
    namespace = "com.fragula2.compose"

    defaultConfig {
        minSdk = 23
//        targetSdk = 33

        consumerProguardFiles("consumer-rules.pro")

//        base.archivesName.set(Gradle.Maven.libraryArtifactId)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
//    implementation(libs.kotlin)

    // Compose
    implementation("androidx.compose.ui:ui:1.4.3")
//    implementation(libs.compose.ui)
    implementation("androidx.compose.foundation:foundation:1.4.3")
//    implementation(libs.compose.foundation)
    implementation("androidx.navigation:navigation-compose:2.5.3")
//    implementation(libs.compose.navigation)

    // Common
    api(project(":fragula-common"))
}