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

package com.fragula2.gradle

object Gradle {

    object Build {
        const val minSdk = 23
        const val targetSdk = 33
        const val compileSdk = 33
        const val buildTools = "33.0.2"
    }

    object Fragula {

        const val minSdk = 21
        const val targetSdk = 33
        const val compileSdk = 33
        const val buildTools = "33.0.2"

        const val libraryVersionName = "2.7"
        const val libraryVersionCode = 16

        var libraryGroupId = ""
        var libraryArtifactId = ""
    }
}