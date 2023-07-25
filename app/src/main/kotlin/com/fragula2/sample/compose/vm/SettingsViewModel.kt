package com.fragula2.sample.compose.vm

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.fragula2.sample.utils.PreferencesManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val swipeDirection = mutableStateOf(PreferencesManager.getSwipeDirection(application))
    val scrimColor = mutableStateOf(PreferencesManager.getScrimColor(application))
    val elevationColor = mutableStateOf(PreferencesManager.getElevationColor(application))
    val scrimAmount = mutableStateOf(PreferencesManager.getScrimAmount(application))
    val elevationAmount = mutableStateOf(PreferencesManager.getElevationAmount(application))
}