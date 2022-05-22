package com.fragula2.compose

import android.view.animation.DecelerateInterpolator

// Same interpolator used in Telegram X
// https://github.com/TGX-Android/X-Android/blob/main/src/main/java/me/vkryl/android/AnimatorUtils.java#L37
class SwipeInterpolator : DecelerateInterpolator(1.78f)