import com.fragula2.gradle.Gradle

plugins {
    id("com.android.test")
    id("kotlin-android")
}

android {
    compileSdk = Gradle.Build.compileSdk
    buildToolsVersion = Gradle.Build.buildTools

    namespace = "com.fragula2.benchmark"
    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    defaultConfig {
        minSdk = Gradle.Build.minSdk
        targetSdk = Gradle.Build.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += "release"
        }
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

    // Tests
    implementation(libs.test.junit)
    implementation(libs.test.junit.ext)
    implementation(libs.test.runner)
    implementation(libs.test.macrobenchmark)
}

androidComponents {
    beforeVariants {
        it.enable = it.buildType == "benchmark"
    }
}