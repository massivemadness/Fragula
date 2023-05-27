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

package com.fragula2.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.common.SwipeDirection
import kotlin.math.abs

class SwipeTransformer(
    private val swipeDirection: SwipeDirection,
    private val parallaxFactor: Float,
    private val alphaFactor: Float,
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
            // Анимация ухода текущего фрагмента при открытии нового
            position > -1 && position <= 0 -> {
                when (swipeDirection) {
                    SwipeDirection.LEFT_TO_RIGHT -> {
                        page.translationX = -page.width * position / parallaxFactor
                    }
                    SwipeDirection.RIGHT_TO_LEFT -> {
                        page.translationX = page.width * position / parallaxFactor
                    }
                    SwipeDirection.TOP_TO_BOTTOM -> {
                        page.translationY = -page.height * position / parallaxFactor
                    }
                    SwipeDirection.BOTTOM_TO_TOP -> {
                        page.translationY = page.height * position / parallaxFactor
                    }
                }
                page.visibility = View.VISIBLE
                page.alpha = 1.0f - abs(position * alphaFactor)
            }
            // Все фрагменты слева от текущего
            else -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
        }
    }
}