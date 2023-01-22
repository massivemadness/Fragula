package com.fragula2.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

enum class SwipeState {
    SWIPE_IN,
    SWIPE_OUT,
    FOLLOW_POINTER,
}

@Composable
fun rememberSwipeState(initialState: SwipeState = SwipeState.FOLLOW_POINTER) =
    rememberSaveable { mutableStateOf(initialState) }