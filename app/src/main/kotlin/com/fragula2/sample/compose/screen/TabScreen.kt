package com.fragula2.sample.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun TabScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // ChatItem("Tab Screen")
    }
}