package com.fragula2.sample.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fragula2.compose.FragulaNavHost
import com.fragula2.compose.rememberFragulaNavController
import com.fragula2.compose.swipeable
import com.fragula2.sample.compose.screen.DetailsScreen
import com.fragula2.sample.compose.screen.ListScreen
import com.fragula2.sample.compose.screen.ProfileScreen
import com.fragula2.sample.compose.screen.TabScreen
import com.fragula2.sample.compose.ui.FragulaTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FragulaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberFragulaNavController()
                    var arrowProgress by remember { mutableStateOf(0f) }
                    Scaffold(
                        topBar = {
                            FragulaAppBar(
                                arrowProgress = arrowProgress,
                                onClick = {
                                    if (arrowProgress == 0f) {
                                        Toast.makeText(this, "Open drawer", Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
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
                            }
                        ) {
                            swipeable("list") {
                                ListScreen(navController)
                            }
                            swipeable("details/{chatId}", arguments = listOf(
                                navArgument("chatId") { NavType.StringType }
                            )) { backStackEntry ->
                                DetailsScreen(
                                    navController = navController,
                                    chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                                )
                            }
                            swipeable("profile/{chatId}", arguments = listOf(
                                navArgument("chatId") { NavType.StringType }
                            )) { backStackEntry ->
                                ProfileScreen(
                                    navController = navController,
                                    chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                                )
                            }
                            swipeable("tab/{text}", arguments = listOf(
                                navArgument("text") { NavType.StringType }
                            )) { backStackEntry ->
                                TabScreen(
                                    navController = navController,
                                    text = backStackEntry.arguments?.getString("text") ?: ""
                                )
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
        }
    )
}