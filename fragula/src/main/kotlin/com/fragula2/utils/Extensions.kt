package com.fragula2.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.adapter.StackEntry
import com.fragula2.navigation.SwipeBackDestination

internal var ViewPager2.pageOverScrollMode: Int
    @RestrictTo(LIBRARY_GROUP)
    get() = getChildAt(0).overScrollMode
    @RestrictTo(LIBRARY_GROUP)
    set(value) { getChildAt(0).overScrollMode = value }

@RestrictTo(LIBRARY_GROUP)
internal fun ViewPager2.fakeDragTo(page: Int, scrollDuration: Long, block: () -> Unit) {
    ValueAnimator.ofInt(0, width * (page - currentItem)).apply {
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
                block()
            }
        })
        interpolator = AccelerateDecelerateInterpolator()
        duration = scrollDuration
        start()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun Activity.requestViewLock(locked: Boolean) {
    if (locked) {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    } else {
        window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun NavBackStackEntry.toStackEntry(): StackEntry {
    val destination = destination as SwipeBackDestination
    return StackEntry(
        id = this.id,
        className = destination.className,
        arguments = this.arguments
    )
}