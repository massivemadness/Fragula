package com.fragula2.sample.compose.viewmodel

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.fragula2.common.SwipeDirection

class SettingsViewModel : ViewModel() {
    val swipeDirection = mutableIntStateOf(SwipeDirection.LEFT_TO_RIGHT.value)
    val elevationAmount = mutableFloatStateOf(3.dp.value)
    val scrimColor = mutableIntStateOf(Color(0xFF000000).toArgb())
    val scrimAmount = mutableFloatStateOf(0.15f)
}