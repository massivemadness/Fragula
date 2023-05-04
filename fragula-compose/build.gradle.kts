
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 33

//    group = ""
//    version = "2.6"
    namespace = "com.fragula2.compose"

    defaultConfig {
        minSdk = 23
//        targetSdk = 33
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
    // Compose
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    // Common
    api(project(":fragula-common"))
}