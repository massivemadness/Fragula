package com.blacksquircle.fragula.sample

import android.view.View
import com.blacksquircle.fragula.common.FragmentNavigator
import kotlin.math.abs

class CustomPageTransformer : FragmentNavigator.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            cameraDistance = width * 100f
            pivotY = height / 2f
            when {
                position > 0 && position < 0.99 -> {
                    alpha = 1f
                    rotationY = position * 150
                    pivotX = width / 2f
                }
                position > -1 && position <= 0 -> {
                    alpha = 1.0f - abs(position * 0.7f)
                    translationX = -width * position
                    rotationY = position * 30
                    pivotX = width.toFloat()
                }
            }
        }
    }
}