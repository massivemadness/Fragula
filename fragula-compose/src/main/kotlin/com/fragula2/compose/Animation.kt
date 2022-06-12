package com.fragula2.compose

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.animateDrag(
    containerWidth: Float,
    onScrollChanged: (Float) -> Unit = {},
    onSwipeCancel: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    enabled: Boolean = true,
): Modifier = composed {
    if (!enabled) return@composed this

    var swipeOffset by remember { mutableStateOf(0f) }
    var gestureConsumed by remember { mutableStateOf(false) }

    pointerInput(Unit) {
        detectHorizontalDragGestures(
            onHorizontalDrag = { _, dragAmount ->
                // dragAmount: positive when scrolling right; negative when scrolling left
                swipeOffset += dragAmount
                when {
                    swipeOffset > containerWidth -> {
                        // offset > 0 when swipe right
                        if (!gestureConsumed) {
                            onSwipeRight()
                            gestureConsumed = true
                        }
                    }
                    swipeOffset < -containerWidth -> {
                        // offset < 0 when swipe left
                        if (!gestureConsumed) {
                            onSwipeLeft()
                            gestureConsumed = true
                        }
                    }
                }
                onScrollChanged(swipeOffset)
            },
            onDragEnd = {
                if (!gestureConsumed) {
                    onSwipeCancel()
                }
                swipeOffset = 0f
                gestureConsumed = false
            }
        )
    }
}

internal fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }