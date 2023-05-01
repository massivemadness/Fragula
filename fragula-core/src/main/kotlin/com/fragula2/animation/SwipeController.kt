package com.fragula2.animation

interface SwipeController {

    fun setScrollingEnabled(enabled: Boolean)
    fun isScrollingEnabled(): Boolean

    @Deprecated("Replace with `setScrollingEnabled`", ReplaceWith("setScrollingEnabled(enabled)"))
    fun setUserInputEnabled(enabled: Boolean)

    @Deprecated("Replace with `isScrollingEnabled`", ReplaceWith("isScrollingEnabled()"))
    fun isUserInputEnabled(): Boolean

    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}