package com.fragula2

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class SwipeBackPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when {
            position > 0 && position < 1 -> {
                page.alpha = 1f
                page.translationX = 0f
            }
            position > -1 && position <= 0 -> {
                page.alpha = 1.0f - abs(position * 0.7f)
                page.translationX = -page.width * position / 1.3F
            }
        }
    }
}