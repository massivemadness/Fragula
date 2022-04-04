package com.fragula2.animation

interface SwipeController {

    fun setUserInputEnabled(enabled: Boolean)
    fun isUserInputEnabled(): Boolean

    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}