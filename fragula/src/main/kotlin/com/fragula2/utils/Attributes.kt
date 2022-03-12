package com.fragula2.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun Context.resolveColor(
    @AttrRes attr: Int,
    @ColorRes defaultValue: Int,
): Int {
    val attributes = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        val result = attributes.getColor(0, 0)
        if (result == 0) {
            return ContextCompat.getColor(this, defaultValue)
        }
        return result
    } finally {
        attributes.recycle()
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun Context.resolveFloat(
    @AttrRes attr: Int,
    @DimenRes defaultValue: Int,
): Float {
    val attributes = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return attributes.getFloat(0, ResourcesCompat.getFloat(resources, defaultValue))
    } finally {
        attributes.recycle()
    }
}