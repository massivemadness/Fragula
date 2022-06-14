package com.fragula2.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class SwipeState {
    MOVE_TO_START,
    MOVE_TO_END,
    FOLLOW_POINTER,
}

@Composable
fun rememberSwipeState(initialState: SwipeState = SwipeState.FOLLOW_POINTER) =
    remember { mutableStateOf(initialState) }