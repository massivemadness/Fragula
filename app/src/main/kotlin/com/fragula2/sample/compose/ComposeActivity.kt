package com.fragula2.sample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fragula2.compose.FragulaNavHost
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
                    val navController = rememberNavController()
                    FragulaNavHost(navController, startDestination = "list") {
                        swipeable("list") {
                            ListScreen(navController)
                        }
                        swipeable("details/{itemId}", arguments = listOf(
                            navArgument("itemId") { NavType.StringType }
                        )) { backStackEntry ->
                            DetailsScreen(
                                navController = navController,
                                chat = backStackEntry.arguments?.getString("itemId") ?: ""
                            )
                        }
                        swipeable("profile") {
                            ProfileScreen(navController)
                        }
                        swipeable("tab") {
                            TabScreen(navController)
                        }
                    }
                }
            }
        }
    }
}