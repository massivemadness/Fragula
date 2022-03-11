package com.fragula2.navigation

import com.fragula2.utils.OnSwipeListener

interface Swipeable {
    fun addOnSwipeListener(listener: OnSwipeListener)
    fun removeOnSwipeListener(listener: OnSwipeListener)
}