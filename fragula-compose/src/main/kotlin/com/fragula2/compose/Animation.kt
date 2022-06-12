package com.fragula2.compose

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing

internal fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }