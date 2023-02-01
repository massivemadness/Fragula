package com.fragula2.benchmark

import android.content.Intent
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry

fun MacrobenchmarkScope.startActivityAndWait(appVariant: AppVariant) {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intentToResolve = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setPackage(packageName)
    }
    val activities = context.packageManager.queryIntentActivities(intentToResolve, 0)
    val intent = Intent(intentToResolve).apply {
        val activityInfo = when (appVariant) {
            AppVariant.XML -> activities[0].activityInfo // MainActivity
            AppVariant.COMPOSE -> activities[1].activityInfo // ComposeActivity
        }
        setClassName(activityInfo.packageName, activityInfo.name)
    }
    startActivityAndWait(intent)
}

enum class AppVariant {
    XML,
    COMPOSE
}