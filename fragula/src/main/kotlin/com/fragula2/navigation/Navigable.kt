package com.fragula2.navigation

import com.fragula2.adapter.StackEntry

interface Navigable {
    fun navigate(entry: StackEntry)
    fun popBackStack(popUpTo: StackEntry)
}