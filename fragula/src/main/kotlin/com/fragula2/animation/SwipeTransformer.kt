package com.fragula2.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class SwipeTransformer(private val alphaMultiplier: Float) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when {
            // Все фрагменты справа от текущего
            position <= -1 -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
            // Экран, который появляется справа от текущего при открытии нового фрагмента
            position > 0 && position < 1 -> {
                page.visibility = View.VISIBLE
                page.translationX = 0f
                page.alpha = 1.0f
            }
            // Анимация ухода текущего фрагмента справо при открытии нового
            position > -1 && position <= 0 -> {
                page.visibility = View.VISIBLE
                page.translationX = -page.width * position / SCROLL_FACTOR
                page.alpha = 1.0f - abs(position * alphaMultiplier)
            }
            // Все фрагменты слева от текущего
            else -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
        }
    }

    companion object {
        private const val SCROLL_FACTOR = 1.3f
    }
}