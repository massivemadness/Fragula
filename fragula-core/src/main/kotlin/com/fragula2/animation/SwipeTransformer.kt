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
import androidx.annotation.CallSuper
import com.fragula2.common.SwipeDirection
import com.fragula2.viewpager2.widget.ViewPager2
import kotlin.math.abs

abstract class SwipeTransformer(
    private val swipeDirection: SwipeDirection,
    private val parallaxFactor: Float,
    private val scrimFactor: Float,
    private val alphaFactor: Float,
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when {
            // All fragments to the right of the current one
            position <= -1 -> {
                page.visibility = View.INVISIBLE
                page.alpha = calculateAlpha(position)
            }
            // The screen that appears to the right of the current one when opening a new fragment
            position > 0 && position < 1 -> {
                when (swipeDirection) {
                    SwipeDirection.LEFT_TO_RIGHT,
                    SwipeDirection.RIGHT_TO_LEFT, -> {
                        page.translationX = 0f
                    }
                    SwipeDirection.TOP_TO_BOTTOM,
                    SwipeDirection.BOTTOM_TO_TOP, -> {
                        page.translationY = 0f
                    }
                }
                page.visibility = View.VISIBLE
                page.alpha = calculateAlpha(position)
            }
            // Animation for the current fragment exiting when opening a new one
            position > -1 && position <= 0 -> {
                val mParallaxFactor = if (isCloseWithAlpha()) 1f else parallaxFactor
                when (swipeDirection) {
                    SwipeDirection.LEFT_TO_RIGHT -> {
                        page.translationX = -page.width * position / mParallaxFactor
                    }
                    SwipeDirection.RIGHT_TO_LEFT -> {
                        page.translationX = page.width * position / mParallaxFactor
                    }
                    SwipeDirection.TOP_TO_BOTTOM -> {
                        page.translationY = -page.height * position / mParallaxFactor
                    }
                    SwipeDirection.BOTTOM_TO_TOP -> {
                        page.translationY = page.height * position / mParallaxFactor
                    }
                }
                page.visibility = View.VISIBLE
                // Add scrim to the exit page
                page.alpha = 1.0f - abs(position * scrimFactor)
            }
            // All fragments to the left of the current one
            else -> {
                page.visibility = View.INVISIBLE
                page.alpha = 1.0f
            }
        }
    }

    private fun calculateAlpha(position: Float): Float =
        if (isCloseWithAlpha()) 1.0f - abs(position * alphaFactor) else 1.0f

    @CallSuper
    open fun isCloseWithAlpha(): Boolean = alphaFactor != 0f
}