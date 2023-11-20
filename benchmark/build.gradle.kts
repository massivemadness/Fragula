/*
 * Copyright 2023 Fragula contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fragula2.gradle.BuildConst

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    id("stub-module")
}

android {
    compileSdk = BuildConst.COMPILE_SDK
    namespace = "com.fragula2.benchmark"

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    defaultConfig {
        minSdk = BuildConst.MIN_SDK
        targetSdk = BuildConst.TARGET_SDK

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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