package com.fragula2.utils

import androidx.fragment.app.Fragment
import com.fragula2.FragulaNavHostFragment
import com.fragula2.animation.SwipeController

fun Fragment.findSwipeController(): SwipeController =
    FragulaNavHostFragment.findSwipeController(this)