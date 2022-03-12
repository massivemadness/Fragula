package com.fragula2.utils

interface SwipeController {
    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}