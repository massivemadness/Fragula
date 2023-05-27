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

internal fun Modifier.animateDrag(
    enabled: Boolean,
    onDragChanged: (Float) -> Unit = {},
    onDragFinished: (Float) -> Unit = {},
): Modifier = composed {
    if (!enabled) return@composed this
    val velocityTracker = VelocityTracker()
    var dragOffset by remember { mutableStateOf(0f) }
    pointerInput(Unit) {
        detectHorizontalDragGestures(
            onHorizontalDrag = { change, dragAmount ->
                dragOffset += dragAmount
                if (dragOffset < 0f) {
                    dragOffset = 0f
                }
                velocityTracker.addPointerInputChange(change)
                if (change.positionChange() != Offset.Zero) {
                    onDragChanged(dragOffset)
                    change.consume()
                }
            },
            onDragEnd = {
                val velocity = velocityTracker.calculateVelocity().x
                velocityTracker.resetTracking()
                onDragFinished(velocity)
                dragOffset = 0f
            },
        )
    }
}

internal fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }