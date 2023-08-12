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

package com.fragula2.compose

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import com.fragula2.common.SwipeDirection
import kotlin.math.abs

internal fun Modifier.animateDrag(
    enabled: Boolean,
    swipeDirection: SwipeDirection,
    onDragChanged: (Float) -> Unit = {},
    onDragFinished: (Float) -> Unit = {},
): Modifier = composed {
    if (!enabled) return@composed this
    val velocityTracker = VelocityTracker()
    var dragOffset by remember { mutableStateOf(0f) }
    pointerInput(swipeDirection) {
        if (!swipeDirection.isHorizontal()) {
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    dragOffset += dragAmount
                    if (dragOffset < 0f && swipeDirection == SwipeDirection.TOP_TO_BOTTOM) {
                        dragOffset = 0f
                    } else if (dragOffset > 0f && swipeDirection == SwipeDirection.BOTTOM_TO_TOP) {
                        dragOffset = 0f
                    }
                    velocityTracker.addPointerInputChange(change)
                    if (change.positionChange() != Offset.Zero) {
                        onDragChanged(dragOffset)
                        change.consume()
                    }
                },
                onDragEnd = {
                    // Don't allow swipe to the other side (i.e if dragOffset == 0f)
                    val velocity = if (dragOffset == 0f) 0f else velocityTracker.calculateVelocity().y
                    velocityTracker.resetTracking()
                    onDragFinished(abs(velocity)) // Provide the absolute value as it might be negative
                    dragOffset = 0f
                },
            )
        } else {
            detectHorizontalDragGestures(
                onHorizontalDrag = { change, dragAmount ->
                    dragOffset += dragAmount
                    if (dragOffset < 0f && swipeDirection == SwipeDirection.LEFT_TO_RIGHT) {
                        dragOffset = 0f
                    } else if (dragOffset > 0f && swipeDirection == SwipeDirection.RIGHT_TO_LEFT) {
                        dragOffset = 0f
                    }
                    velocityTracker.addPointerInputChange(change)
                    if (change.positionChange() != Offset.Zero) {
                        onDragChanged(dragOffset)
                        change.consume()
                    }
                },
                onDragEnd = {
                    // Don't allow swipe to the other side (i.e if dragOffset == 0f)
                    val velocity = if (dragOffset == 0f) 0f else velocityTracker.calculateVelocity().x
                    velocityTracker.resetTracking()
                    onDragFinished(abs(velocity)) // Provide the absolute value as it might be negative
                    dragOffset = 0f
                },
            )
        }
    }
}

internal fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }