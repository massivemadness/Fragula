package com.fragula2.animation

enum class SwipeDirection(val value: Int) {
    LEFT_TO_RIGHT(0),
    RIGHT_TO_LEFT(1),
    TOP_TO_BOTTOM(2),
    BOTTOM_TO_TOP(3);

    companion object {

        fun find(value: Int): SwipeDirection {
            return checkNotNull(values().find { it.value == value })
        }
    }
}