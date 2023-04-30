buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.plugin.android)
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.safeargs)
        classpath(libs.plugin.validator)
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

apply(from = "gradle/ktlint.gradle.kts")