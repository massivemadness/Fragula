package com.fragulo.navigator.transformer

import android.view.View
import com.fragulo.navigator.common.FragmentNavigator
import com.fragulo.navigator.extensions.invisible
import com.fragulo.navigator.extensions.visible
import kotlin.math.abs


class NavigatorPageTransformer : FragmentNavigator.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position > 0 && position < 0.99 -> {
                    alpha = 1f
                    translationX = 0f
                }
                position <= 0 -> {
                    alpha = 1.0f - abs(position * 0.7f)
                    translationX = -pageWidth * position / 1.3F
                }
            }
        }
    }
}