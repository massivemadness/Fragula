package com.fragula.extensions

import android.view.View


internal fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

internal fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

internal fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}