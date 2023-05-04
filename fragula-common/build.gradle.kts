//import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

//Gradle.Maven.libraryGroupId = "com.fragula2"
//Gradle.Maven.libraryArtifactId = "fragula-common"

//apply(from = "../gradle/publish.gradle")

android {
    compileSdk = 33

    namespace = "com.fragula2.common"

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
}

dependencies {

    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
//    implementation(libs.kotlin)
}