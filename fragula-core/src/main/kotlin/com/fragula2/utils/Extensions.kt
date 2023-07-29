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

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.view.updateLayoutParams
import com.fragula2.R
import com.fragula2.common.SwipeDirection
import com.fragula2.common.SwipeInterpolator
import com.fragula2.viewpager2.widget.ViewPager2

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
    stepForward: Boolean,
    swipeDirection: SwipeDirection,
    scrollDuration: Long,
    onScrollFinished: () -> Unit,
) {
    val page = if (stepForward) currentItem + 1 else currentItem - 1
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
                    val distance = if (stepForward) -currentPxToDrag else currentPxToDrag
                    fakeDragBy(distance)
                }
                SwipeDirection.TOP_TO_BOTTOM -> fakeDragBy(-currentPxToDrag)
                SwipeDirection.BOTTOM_TO_TOP -> {
                    val distance = if (stepForward) -currentPxToDrag else currentPxToDrag
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
                onScrollFinished()
            }
        })
        interpolator = SwipeInterpolator()
        duration = if (stepForward) scrollDuration else scrollDuration - (scrollDuration / 5)
        start()
    }
}

@RestrictTo(LIBRARY_GROUP)
internal fun View.updateLayoutAngle(
    swipeDirection: SwipeDirection,
    elevation: Float,
) {
    updateLayoutParams<FrameLayout.LayoutParams> {
        when (swipeDirection) {
            SwipeDirection.LEFT_TO_RIGHT -> {
                setBackgroundResource(R.drawable.bg_elevation_ltr)
                width = elevation.toInt()
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.RIGHT
            }
            SwipeDirection.RIGHT_TO_LEFT -> {
                setBackgroundResource(R.drawable.bg_elevation_rtl)
                width = elevation.toInt()
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.LEFT
            }
            SwipeDirection.TOP_TO_BOTTOM -> {
                setBackgroundResource(R.drawable.bg_elevation_ttb)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = elevation.toInt()
                gravity = Gravity.BOTTOM
            }
            SwipeDirection.BOTTOM_TO_TOP -> {
                setBackgroundResource(R.drawable.bg_elevation_btt)
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = elevation.toInt()
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
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
    } else {
        window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
    }
}