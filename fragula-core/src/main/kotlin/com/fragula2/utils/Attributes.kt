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

package com.fragula2.utils

import android.content.Context
import androidx.annotation.*
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

@RestrictTo(LIBRARY_GROUP)
internal fun Context.resolveColor(
    @AttrRes attrRes: Int,
    @ColorRes defaultValue: Int,
): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
        val result = attrs.getColor(0, 0)
        if (result == 0) {
            return ContextCompat.getColor(this, defaultValue)
        }
        return result
    } finally {
        attrs.recycle()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun Context.resolveFloat(
    @AttrRes attrRes: Int,
    @DimenRes defaultValue: Int,
): Float {
    val attrs = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
        return attrs.getFloat(0, ResourcesCompat.getFloat(resources, defaultValue))
    } finally {
        attrs.recycle()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun Context.resolveInteger(
    @AttrRes attrRes: Int,
    @IntegerRes defaultValue: Int,
): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
        return attrs.getInteger(0, resources.getInteger(defaultValue))
    } finally {
        attrs.recycle()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun Context.resolveDimen(
    @AttrRes attrRes: Int,
    @DimenRes defaultValue: Int,
): Float {
    val attrs = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
        return attrs.getDimension(0, resources.getDimension(defaultValue))
    } finally {
        attrs.recycle()
    }
}