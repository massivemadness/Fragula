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

package com.fragula2.benchmark

import android.content.Intent
import android.content.pm.PackageManager
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry

fun MacrobenchmarkScope.startActivityAndWait(appVariant: AppVariant) {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intentToResolve = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setPackage(packageName)
    }
    val resolveInfos = context.packageManager
        .queryIntentActivities(intentToResolve, PackageManager.GET_META_DATA)
    val intent = Intent(intentToResolve).apply {
        val activityInfo = when (appVariant) {
            AppVariant.XML -> resolveInfos[0].activityInfo // MainActivity
            AppVariant.COMPOSE -> resolveInfos[1].activityInfo // ComposeActivity
        }
        setClassName(activityInfo.packageName, activityInfo.name)
    }
    startActivityAndWait(intent)
}

enum class AppVariant {
    XML,
    COMPOSE,
}