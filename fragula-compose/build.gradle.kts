
plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
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
//    publishing {
//        publications {
//            create<MavenPublication>("maven") {
//                groupId = "com.fragula2.compose"
//                artifactId = "compose"
//                version = "2.7.0"
//                from(components["kotlin"])
//            }
//        }
//    }
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            register("release", MavenPublication::class) {
                // Applies the component for the release build variant.
                // NOTE : Delete this line code if you publish Native Java / Kotlin Library
                from(components["release"])
                // Library Package Name (Example : "com.frogobox.androidfirstlib")
                // NOTE : Different GroupId For Each Library / Module, So That Each Library Is Not Overwritten
                groupId = "com.fragula2"
                // Library Name / Module Name (Example : "androidfirstlib")
                // NOTE : Different ArtifactId For Each Library / Module, So That Each Library Is Not Overwritten
                artifactId = "compose"
                // Version Library Name (Example : "1.0.0")
                version = "2.7.0"
            }
        }
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