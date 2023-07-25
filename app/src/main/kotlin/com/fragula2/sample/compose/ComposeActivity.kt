/*
 * Copyright 2023 Fragula contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fragula2.sample.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fragula2.common.SwipeDirection
import com.fragula2.compose.FragulaNavHost
import com.fragula2.compose.rememberFragulaNavController
import com.fragula2.compose.swipeable
import com.fragula2.sample.compose.screen.DetailsScreen
import com.fragula2.sample.compose.screen.ListScreen
import com.fragula2.sample.compose.screen.ProfileScreen
import com.fragula2.sample.compose.screen.SettingsScreen
import com.fragula2.sample.compose.screen.TabScreen
import com.fragula2.sample.compose.ui.FragulaTheme
import com.fragula2.sample.compose.vm.SettingsViewModel
import com.fragula2.sample.utils.argbToColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            FragulaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    val navController = rememberFragulaNavController()
                    var arrowProgress by remember { mutableStateOf(0f) }
                    Scaffold(
                        topBar = {
                            FragulaAppBar(
                                arrowProgress = arrowProgress,
                                onClick = {
                                    if (arrowProgress == 0f) {
                                        Toast.makeText(this, "Open", Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.popBackStack()
                                    }
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        },
                    ) { paddingValues ->
                        FragulaNavHost(
                            navController = navController,
                            startDestination = "list",
                            onPageScrolled = { position, positionOffset, _ ->
                                arrowProgress = when {
                                    position > 0 -> 1f
                                    positionOffset > 0 -> 1f - positionOffset
                                    else -> 0f
                                }
                            },
                            swipeDirection = SwipeDirection.of(settingsViewModel.swipeDirection.value),
                            scrimColor = settingsViewModel.scrimColor.value.argbToColor(),
                            elevationColor = settingsViewModel.elevationColor.value.argbToColor(),
                            scrimAmount = settingsViewModel.scrimAmount.value,
                            elevationAmount = settingsViewModel.elevationAmount.value.dp
                        ) {
                            swipeable("list") {
                                ListScreen(navController)
                            }
                            swipeable(
                                route = "details/{chatId}",
                                arguments = listOf(
                                    navArgument("chatId") { NavType.StringType },
                                ),
                            ) { backStackEntry ->
                                DetailsScreen(
                                    navController = navController,
                                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                                )
                            }
                            swipeable(
                                route = "profile/{chatId}",
                                arguments = listOf(
                                    navArgument("chatId") { NavType.StringType },
                                ),
                            ) { backStackEntry ->
                                ProfileScreen(
                                    navController = navController,
                                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                                )
                            }
                            swipeable(
                                route = "tab/{text}",
                                arguments = listOf(
                                    navArgument("text") { NavType.StringType },
                                ),
                            ) { backStackEntry ->
                                TabScreen(
                                    navController = navController,
                                    text = backStackEntry.arguments?.getString("text") ?: "",
                                )
                            }
                            swipeable("settings") {
                                SettingsScreen(settingsViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FragulaAppBar(
    arrowProgress: Float = 0f,
    onClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text("Fragula") },
        navigationIcon = {
            IconButton(onClick = { onClick() }) {
                val icon = DrawerArrowDrawable(LocalContext.current).apply {
                    progress = arrowProgress
                }
                Icon(
                    painter = rememberDrawablePainter(icon),
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, null)
            }
        })
}