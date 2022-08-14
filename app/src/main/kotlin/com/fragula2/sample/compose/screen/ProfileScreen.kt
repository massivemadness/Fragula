package com.fragula2.sample.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.fragula2.sample.compose.ui.getChats

@Composable
fun ProfileScreen(navController: NavController, chatId: String) {
    val chat = getChats().find { it.id == chatId } ?: error("Chat not found")
    Box(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {
        /*ChatItem("Profile Screen") {
            navController.navigate("social")
        }*/
    }
}