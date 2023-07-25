package com.fragula2.sample.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.fragula2.common.SwipeDirection

object PreferencesManager {
    private const val PREFS_NAME = "Fragula"
    private const val KEY_SWIPE_DIRECTION = "KEY_SWIPE_DIRECTION"
    private const val KEY_SCRIM_COLOR = "KEY_SCRIM_COLOR"
    private const val KEY_ELEVATION_COLOR = "KEY_ELEVATION_COLOR"
    private const val KEY_SCRIM_AMOUNT = "KEY_SCRIM_AMOUNT"
    private const val KEY_ELEVATION_AMOUNT = "KEY_ELEVATION_AMOUNT"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSwipeDirection(context: Context, direction: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_SWIPE_DIRECTION, direction)
        editor.apply()
    }

    fun saveScrimColor(context: Context, color: Color) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_SCRIM_COLOR, color.toArgb())
        editor.apply()
    }

    fun saveScrimAmount(context: Context, amount: Float) {
        val editor = getSharedPreferences(context).edit()
        editor.putFloat(KEY_SCRIM_AMOUNT, amount)
        editor.apply()
    }

    fun saveElevationColor(context: Context, color: Color) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_ELEVATION_COLOR, color.toArgb())
        editor.apply()
    }

    fun saveElevationAmount(context: Context, amount: Float) {
        val editor = getSharedPreferences(context).edit()
        editor.putFloat(KEY_ELEVATION_AMOUNT, amount)
        editor.apply()
    }

    fun getSwipeDirection(context: Context): Int =
        getSharedPreferences(context).getInt(KEY_SWIPE_DIRECTION, SwipeDirection.LEFT_TO_RIGHT.value)

    fun getScrimColor(context: Context): Int =
        getSharedPreferences(context).getInt(KEY_SCRIM_COLOR, Color(0xFF000000).toArgb())

    fun getScrimAmount(context: Context): Float =
        getSharedPreferences(context).getFloat(KEY_SCRIM_AMOUNT, 0.15f)

    fun getElevationColor(context: Context): Int =
        getSharedPreferences(context).getInt(KEY_ELEVATION_COLOR, Color(0x00000000).toArgb())

    fun getElevationAmount(context: Context): Float =
        getSharedPreferences(context).getFloat(KEY_ELEVATION_AMOUNT, 3.dp.value)
}