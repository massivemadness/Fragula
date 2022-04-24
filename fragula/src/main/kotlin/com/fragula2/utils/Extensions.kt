package com.fragula2.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.R
import com.fragula2.adapter.StackEntry
import com.fragula2.animation.SwipeDirection
import com.fragula2.navigation.SwipeBackDestination

internal var ViewPager2.pageOverScrollMode: Int
    @RestrictTo(LIBRARY_GROUP)
    get() = getChildAt(0).overScrollMode
    @RestrictTo(LIBRARY_GROUP)
    set(value) { getChildAt(0).overScrollMode = value }

internal var ViewPager2.pageSwipeDirection: SwipeDirection
    @RestrictTo(LIBRARY_GROUP)
    get() = when (orientation) {
        ViewPager2.ORIENTATION_VERTICAL -> {
            if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                SwipeDirection.TOP_TO_BOTTOM
            } else {
                SwipeDirection.BOTTOM_TO_TOP
            }
        }
        ViewPager2.ORIENTATION_HORIZONTAL -> {
            if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                SwipeDirection.LEFT_TO_RIGHT
            } else {
                SwipeDirection.RIGHT_TO_LEFT
            }
        }
        else -> throw IllegalStateException("Unsupported ViewPager2 orientation")
    }
    @RestrictTo(LIBRARY_GROUP)
    set(value) {
        when (value) {
            SwipeDirection.LEFT_TO_RIGHT -> {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            SwipeDirection.RIGHT_TO_LEFT -> {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            SwipeDirection.TOP_TO_BOTTOM -> {
                orientation = ViewPager2.ORIENTATION_VERTICAL
                layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            SwipeDirection.BOTTOM_TO_TOP -> {
                orientation = ViewPager2.ORIENTATION_VERTICAL
                layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
        }
    }

@RestrictTo(LIBRARY_GROUP)
internal fun ViewPager2.fakeDragTo(
    forward: Boolean,
    swipeDirection: SwipeDirection,
    scrollDuration: Long,
    block: () -> Unit
) {
    val page = if (forward) currentItem + 1 else currentItem - 1
    val (from, to) = when (swipeDirection) {
        SwipeDirection.LEFT_TO_RIGHT -> 0 to width
        SwipeDirection.RIGHT_TO_LEFT -> width to 0
        SwipeDirection.TOP_TO_BOTTOM -> 0 to height
        SwipeDirection.BOTTOM_TO_TOP -> height to 0
    }
    ValueAnimator.ofInt(from, to * (page - currentItem)).apply {
        var previousValue = from
        addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            when (swipeDirection) {
                SwipeDirection.LEFT_TO_RIGHT -> fakeDragBy(-currentPxToDrag)
                SwipeDirection.RIGHT_TO_LEFT -> {
                    val distance = if (forward) -currentPxToDrag else currentPxToDrag
                    fakeDragBy(distance)
                }
                SwipeDirection.TOP_TO_BOTTOM -> fakeDragBy(-currentPxToDrag)
                SwipeDirection.BOTTOM_TO_TOP -> {
                    val distance = if (forward) -currentPxToDrag else currentPxToDrag
                    fakeDragBy(distance)
                }
            }
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
        interpolator = if (forward) {
            AccelerateDecelerateInterpolator()
        } else {
            DecelerateInterpolator(1.2f)
        }
        duration = scrollDuration
        start()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun View.updateLayoutAngle(swipeDirection: SwipeDirection) {
    val viewSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 3f, // dp
        context.resources.displayMetrics
    )
    updateLayoutParams<FrameLayout.LayoutParams> {
        when (swipeDirection) {
            SwipeDirection.LEFT_TO_RIGHT -> {
                setBackgroundResource(R.drawable.bg_elevation_ltr)
                width = viewSize.toInt()
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.RIGHT
            }
            SwipeDirection.RIGHT_TO_LEFT -> {
                setBackgroundResource(R.drawable.bg_elevation_rtl)
                width = viewSize.toInt()
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.LEFT
            }
            SwipeDirection.TOP_TO_BOTTOM -> {
                setBackgroundResource(R.drawable.bg_elevation_ttb)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = viewSize.toInt()
                gravity = Gravity.BOTTOM
            }
            SwipeDirection.BOTTOM_TO_TOP -> {
                setBackgroundResource(R.drawable.bg_elevation_btt)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = viewSize.toInt()
                gravity = Gravity.TOP
            }
        }
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