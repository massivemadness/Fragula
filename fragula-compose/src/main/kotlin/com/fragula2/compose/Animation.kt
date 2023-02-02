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
    onScrollChanged: (Float) -> Unit = {},
    onScrollCancelled: (Float) -> Unit = {},
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
                    onScrollChanged(dragOffset)
                    change.consume()
                }
            },
            onDragEnd = {
                val velocity = velocityTracker.calculateVelocity().x
                velocityTracker.resetTracking()
                onScrollCancelled(velocity)
                dragOffset = 0f
            },
        )
    }
}

internal fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }