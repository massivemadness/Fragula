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

enum class SwipeDirection(val value: Int) {
    LEFT_TO_RIGHT(0),
    RIGHT_TO_LEFT(1),
    TOP_TO_BOTTOM(2),
    BOTTOM_TO_TOP(3);

    fun isHorizontal(): Boolean = (this == LEFT_TO_RIGHT || this == RIGHT_TO_LEFT)

    fun isRTL(): Boolean = (this == RIGHT_TO_LEFT || this == BOTTOM_TO_TOP)

    companion object {

        fun of(value: Int): SwipeDirection {
            return checkNotNull(values().find { it.value == value })
        }
    }
}