package com.fragula2.navigation

import com.fragula2.adapter.FragulaEntry

interface SwipeBackInterface {
    fun navigate(entry: FragulaEntry)
    fun popBackStack()
}