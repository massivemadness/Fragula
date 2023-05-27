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

import com.fragula2.gradle.Gradle

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("binary-compatibility-validator")
}

Gradle.Fragula.libraryGroupId = "com.fragula2"
Gradle.Fragula.libraryArtifactId = "fragula-core"

apply(from = "../gradle/publish.gradle")

android {
    compileSdk = Gradle.Fragula.compileSdk
    buildToolsVersion = Gradle.Fragula.buildTools

    group = Gradle.Fragula.libraryGroupId
    version = Gradle.Fragula.libraryVersionName
    namespace = "com.fragula2"

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