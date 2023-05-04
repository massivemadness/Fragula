buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
//        classpath(libs.plugin.android)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
//        classpath(libs.plugin.kotlin)
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
//        classpath(libs.plugin.safeargs)
        classpath("org.jetbrains.kotlinx:binary-compatibility-validator:0.13.0")
//        classpath(libs.plugin.validator)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

//apply(from = "gradle/ktlint.gradle.kts")