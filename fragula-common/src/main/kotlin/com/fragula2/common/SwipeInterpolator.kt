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

package com.fragula2.common

import android.view.animation.DecelerateInterpolator

// Same interpolator used in Telegram X
// https://github.com/TGX-Android/X-Android/blob/main/src/main/java/me/vkryl/android/AnimatorUtils.java#L38
class SwipeInterpolator : DecelerateInterpolator(1.78f)