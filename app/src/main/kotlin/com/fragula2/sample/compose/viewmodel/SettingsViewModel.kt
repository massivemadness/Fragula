package com.fragula2.sample.compose.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.fragula2.common.SwipeDirection

class SettingsViewModel : ViewModel() {
    val swipeDirection = mutableStateOf(SwipeDirection.LEFT_TO_RIGHT.value)
    val elevationAmount = mutableStateOf(3.dp.value)
    val scrimColor = mutableStateOf(Color(0xFF000000).toArgb())
    val scrimAmount = mutableStateOf(0.15f)
}