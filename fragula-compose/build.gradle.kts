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

plugins {
    id("fragula-module")
    id("publish-module")
}

android {
    namespace = "com.fragula2.compose"

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    buildFeatures {
        compose = true
    }
}

publishModule {
    libraryGroup = "com.fragula2"
    libraryArtifact = "fragula-compose"
    libraryVersion = "2.9"
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