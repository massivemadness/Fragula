import com.fragula2.gradle.Gradle

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = Gradle.Build.compileSdk
    buildToolsVersion = Gradle.Build.buildTools

    namespace = "com.fragula2.sample"

    defaultConfig {
        applicationId = "com.fragula2.sample"

        minSdk = Gradle.Build.minSdk
        targetSdk = Gradle.Build.targetSdk

        versionCode = 10000
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("benchmark") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            proguardFiles("benchmark-rules.pro")
        }
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
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.profileinstaller)

    // UI
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.coil)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)
    implementation(libs.compose.preview)
    implementation(libs.compose.drawable)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.manifest)

    // AAC
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.navigation)

    // Modules
    implementation(project(":fragula-compose"))
    implementation(project(":fragula-core"))

    // Tests
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}