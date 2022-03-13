package com.fragula2.animation

interface SwipeController {
    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}