package com.fragula2.sample.compose.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.fragula2.sample.compose.viewmodel.SettingsViewModel
import com.fragula2.sample.utils.argbToColor

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val directionsList = listOf(0f, 1f, 2f, 3f) // SwipeDirection values
    val scrimAmountList = listOf(0.15f, 0.30f, 0.45f, 0.60f, 0.75f, 0.90f, 1f)
    val elevationAmountList = listOf(0f, 1f, 3f, 6f, 9f, 12f, 15f, 18f)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
            .padding(bottom = 12.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            text = "Swipe Direction",
        )
        FloatButtonsRow(
            modifier = Modifier.padding(top = 10.dp),
            values = directionsList,
            selectedFloat = settingsViewModel.swipeDirection.value.toFloat(),
        ) { value ->
            settingsViewModel.swipeDirection.value = value.toInt()
        }

        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            text = "Scrim Color",
        )
        ColorButtonsRow(
            modifier = Modifier.padding(top = 10.dp),
            selectedColor = settingsViewModel.scrimColor.value.argbToColor(),
        ) { color ->
            settingsViewModel.scrimColor.value = color.toArgb()
        }

        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            text = "Scrim Amount",
        )
        FloatButtonsRow(
            modifier = Modifier.padding(top = 10.dp),
            values = scrimAmountList,
            selectedFloat = settingsViewModel.scrimAmount.value,
        ) { amount ->
            settingsViewModel.scrimAmount.value = amount
        }

        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            text = "Elevation Amount",
        )
        FloatButtonsRow(
            modifier = Modifier.padding(top = 10.dp),
            values = elevationAmountList,
            selectedFloat = settingsViewModel.elevationAmount.value,
        ) { amount ->
            settingsViewModel.elevationAmount.value = amount
        }
    }
}

@Composable
private fun ColorButtonsRow(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    onColorClick: (value: Color) -> Unit = {},
) {
    val colors = listOf(
        Color(0xFF000000),
        Color(0x00000000),
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Magenta,
        Color.White,
    )
    var selectedValue: Color by remember { mutableStateOf(selectedColor) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Create a custom ButtonDefaults object to set contentPadding to zero
        for (color in colors) {
            Button(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
                    selectedValue = color
                    onColorClick(color)
                },
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                border = if (selectedValue == color) BorderStroke(2.dp, Color.Gray) else null,
                colors = ButtonDefaults.buttonColors(backgroundColor = color),
            ) {}
        }
    }
}

@Composable
private fun FloatButtonsRow(
    modifier: Modifier = Modifier,
    values: List<Float>,
    selectedFloat: Float,
    onButtonClick: (value: Float) -> Unit = {},
) {
    var selectedValue: Float by remember { mutableStateOf(selectedFloat) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        for (value in values) {
            Button(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
                    selectedValue = value
                    onButtonClick(value)
                },
                shape = RoundedCornerShape(8.dp),
                border = if (selectedValue == value) BorderStroke(1.dp, Color.Gray) else null,
            ) {
                Text(text = value.toString())
            }
        }
    }
}