package com.fragula2.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.adapter.FragulaEntry
import com.fragula2.navigation.SwipeBackNavigator

private const val SCROLL_DURATION = 300L

// RecyclerView overscroll
internal var ViewPager2.pageOverScrollMode: Int
    get() = getChildAt(0).overScrollMode
    set(value) { getChildAt(0).overScrollMode = value }

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

internal fun NavBackStackEntry.toFragulaEntry(): FragulaEntry {
    val destination = destination as SwipeBackNavigator.Destination
    return FragulaEntry(
        className = destination.className,
        arguments = this.arguments
    )
}