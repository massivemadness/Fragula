package com.fragula2.compose

import androidx.navigation.NavBackStackEntry

internal data class BackTo(
    val popUpTo: NavBackStackEntry,
    val saveState: Boolean,
)