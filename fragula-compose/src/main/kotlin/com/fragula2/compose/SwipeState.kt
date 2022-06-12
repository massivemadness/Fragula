package com.fragula2.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

enum class SwipeState {
    MOVE_TO_START,
    MOVE_TO_END,
    FOLLOW_POINTER,
}

@Composable
internal fun rememberSwipeState() = rememberSaveable {
    mutableStateOf(SwipeState.FOLLOW_POINTER)
}