package com.fragula2.animation

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

class SwipeInterpolator(private val stepForward: Boolean) : Interpolator {

    private val stepForwardInterpolator = AccelerateDecelerateInterpolator()
    private val stepBackInterpolator = DecelerateInterpolator(1.2f)

    override fun getInterpolation(input: Float): Float {
        return if (stepForward) {
            stepForwardInterpolator.getInterpolation(input)
        } else {
            stepBackInterpolator.getInterpolation(input)
        }
    }
}