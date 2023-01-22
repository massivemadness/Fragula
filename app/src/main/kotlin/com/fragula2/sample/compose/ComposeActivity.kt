package com.fragula2.sample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fragula2.compose.FragulaNavHost
import com.fragula2.compose.rememberSwipeBackNavigator
import com.fragula2.compose.swipeable
import com.fragula2.sample.compose.screen.DetailsScreen
import com.fragula2.sample.compose.screen.ListScreen
import com.fragula2.sample.compose.screen.ProfileScreen
import com.fragula2.sample.compose.screen.TabScreen
import com.fragula2.sample.compose.ui.FragulaTheme

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FragulaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Fragula") }
                            )
                        }
                    ) { paddingValues ->
                        val swipeBackNavigator = rememberSwipeBackNavigator()
                        val navController = rememberNavController(swipeBackNavigator)
                        FragulaNavHost(navController, startDestination = "list") {
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