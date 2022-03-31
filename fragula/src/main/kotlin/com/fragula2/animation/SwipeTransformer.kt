package com.fragula2.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class SwipeTransformer(
    private val swipeDirection: SwipeDirection,
    private val alphaMultiplier: Float,
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when {
            // Все фрагменты справа от текущего
            position <= -1 -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
            // Экран, который появляется справа от текущего при открытии нового фрагмента
            position > 0 && position < 1 -> {
                when (swipeDirection) {
                    SwipeDirection.LEFT_TO_RIGHT,
                    SwipeDirection.RIGHT_TO_LEFT -> {
                        page.translationX = 0f
                    }
                    SwipeDirection.TOP_TO_BOTTOM,
                    SwipeDirection.BOTTOM_TO_TOP -> {
                        page.translationY = 0f
                    }
                }
                page.visibility = View.VISIBLE
                page.alpha = 1.0f
            }
            // Анимация ухода текущего фрагмента справо при открытии нового
            position > -1 && position <= 0 -> {
                when (swipeDirection) {
                    SwipeDirection.LEFT_TO_RIGHT -> {
                        page.translationX = -page.width * position / SCROLL_FACTOR
                    }
                    SwipeDirection.RIGHT_TO_LEFT -> {
                        page.translationX = page.width * position / SCROLL_FACTOR
                        page.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
                    }
                    SwipeDirection.TOP_TO_BOTTOM -> {
                        page.translationY = -page.height * position / SCROLL_FACTOR
                    }
                    SwipeDirection.BOTTOM_TO_TOP -> {
                        page.translationY = page.height * position / SCROLL_FACTOR
                        page.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
                    }
                }
                page.visibility = View.VISIBLE
                page.alpha = 1.0f - abs(position * alphaMultiplier)
            }
            // Все фрагменты слева от текущего
            else -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
        }
    }

    private companion object {
        private const val SCROLL_FACTOR = 1.3f
    }
}