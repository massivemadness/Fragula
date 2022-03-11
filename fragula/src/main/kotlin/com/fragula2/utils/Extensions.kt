package com.fragula2.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.*
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.adapter.FragulaEntry
import com.fragula2.navigation.SwipeBackNavigator

private const val SCROLL_DURATION = 300L

// RecyclerView overscroll
internal var ViewPager2.pageOverScrollMode: Int
    get() = getChildAt(0).overScrollMode
    set(value) { getChildAt(0).overScrollMode = value }

@RestrictTo(LIBRARY_GROUP)
internal fun ViewPager2.setCurrentItemInternal(moveTo: Int, onAnimationEnd: () -> Unit) {
    ValueAnimator.ofInt(0, width * (moveTo - currentItem)).apply {
        var previousValue = 0
        addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator) = Unit
            override fun onAnimationRepeat(animation: Animator) = Unit
            override fun onAnimationStart(animation: Animator) {
                beginFakeDrag()
            }
            override fun onAnimationEnd(animation: Animator) {
                endFakeDrag()
                onAnimationEnd()
            }
        })
        interpolator = AccelerateDecelerateInterpolator()
        duration = SCROLL_DURATION
        start()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun NavBackStackEntry.toFragulaEntry(): FragulaEntry {
    val destination = destination as SwipeBackNavigator.Destination
    return FragulaEntry(
        className = destination.className,
        arguments = this.arguments
    )
}

@RestrictTo(LIBRARY_GROUP)
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

@RestrictTo(LIBRARY_GROUP)
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