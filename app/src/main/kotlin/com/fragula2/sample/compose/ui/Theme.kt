package com.fragula2.sample.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DarkPrimary,
    primaryVariant = DarkPrimaryVariant,
    secondary = DarkSecondary,
    secondaryVariant = DarkSecondaryVariant,
    surface = DarkSurface,
    error = DarkError,
    background = DarkBackground,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onSurface = DarkOnSurface,
    onError = DarkOnError,
    onBackground = DarkOnBackground,
)

private val LightColorPalette = lightColors(
    primary = LightPrimary,
    primaryVariant = LightPrimaryVariant,
    secondary = LightSecondary,
    secondaryVariant = LightSecondaryVariant,
    surface = LightSurface,
    error = LightError,
    background = LightBackground,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onSurface = LightOnSurface,
    onError = LightOnError,
    onBackground = LightOnBackground,
)

@Composable
fun FragulaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}