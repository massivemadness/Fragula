package com.fragula2.animation

interface SwipeController {

    fun setScrollingEnabled(enabled: Boolean)
    fun isScrollingEnabled(): Boolean

    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}