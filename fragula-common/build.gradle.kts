plugins {
    id("com.android.library")
    kotlin("android")
}

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
}

dependencies {
    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
}