package com.fragula2.animation

fun interface OnSwipeListener {

    /**
     * This method will be invoked when the current page is scrolled, either as part of a
     * programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * [position] Position index of the first page currently being displayed.
     *            Page position+1 will be visible if positionOffset is nonzero.
     * [positionOffset] Value from [0, 1) indicating the offset from the page at position.
     * [positionOffsetPixels] Value in pixels indicating the offset from position.
     */
    fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    )
}