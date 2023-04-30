package com.fragula2.gradle

object Gradle {

    object Build {
        const val minSdk = 23
        const val targetSdk = 33
        const val compileSdk = 33
        const val buildTools = "33.0.2"
    }

    object Maven {

        const val libraryVersionName = "2.6"
        const val libraryVersionCode = 15

        var libraryGroupId = ""
        var libraryArtifactId = ""
    }
}