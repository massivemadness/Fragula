package com.fragula2.navigation

import androidx.navigation.NavBackStackEntry

interface Navigable {
    fun navigate(entry: NavBackStackEntry)
    fun popBackStack(
        popUpTo: NavBackStackEntry,
        onScrollFinished: () -> Unit,
    )
    fun isAnimating(): Boolean
}