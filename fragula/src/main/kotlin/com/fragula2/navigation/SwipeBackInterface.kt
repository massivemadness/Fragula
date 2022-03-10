package com.fragula2.navigation

import com.fragula2.adapter.FragulaEntry

internal interface SwipeBackInterface {
    fun navigate(entry: FragulaEntry)
    fun popBackStack()
}