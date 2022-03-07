package com.blacksquircle.fragula.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blacksquircle.navigator.R

internal fun Context.getColorCompat(@ColorRes colorRes: Int) =
    ContextCompat.getColor(this, colorRes)

internal fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

internal inline val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels

internal inline val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels

internal fun Context?.getStatusBarHeight(): Int {
    this?.let {
        val resources = this.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    } ?: run { return 0 }
}

internal fun Context?.getToolbarHeight(): Int {
    this?.let {
        val attrs = intArrayOf(R.attr.actionBarSize)
        val ta = this.obtainStyledAttributes(attrs)
        val toolBarHeight = ta.getDimensionPixelSize(0, -1)
        ta.recycle()
        return toolBarHeight
    } ?: run { return 0 }
}