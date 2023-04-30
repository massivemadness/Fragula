import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("binary-compatibility-validator")
}

Gradle.Maven.libraryGroupId = "com.fragula2"
Gradle.Maven.libraryArtifactId = "fragula-core"

apply(from = "../gradle/publish.gradle")

android {
    compileSdk = Gradle.Build.compileSdk
    buildToolsVersion = Gradle.Build.buildTools

    group = Gradle.Maven.libraryGroupId
    version = Gradle.Maven.libraryVersionName
    namespace = "com.fragula2"

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
    sourceSets {
        named("main") {
            java.srcDir("src/main/kotlin")
            res.srcDirs("src/main/res", "src/main/res-public")
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
    implementation(libs.kotlin)

    // UI
    implementation(libs.androidx.viewpager2)

    // AAC
    implementation(libs.androidx.navigation)

    // Common
    api(project(":fragula-common"))
}