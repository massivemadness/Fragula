package com.fragula2.sample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fragula2.compose.FragulaNavHost
import com.fragula2.compose.rememberSwipeBackNavigator
import com.fragula2.compose.swipeable

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val swipeBackNavigator = rememberSwipeBackNavigator()
                    val navController = rememberNavController(swipeBackNavigator)
                    FragulaNavHost(navController, startDestination = "list") {
                        swipeable("list") {
                            ListScreen(navController)
                        }
                        swipeable("details/{itemId}", arguments = listOf(
                            navArgument("itemId") { NavType.StringType }
                        )) { backStackEntry ->
                            DetailsScreen(
                                navController = navController,
                                itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                            )
                        }
                        swipeable("profile") {
                            ProfileScreen(navController)
                        }
                        swipeable("social") {
                            SocialScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListScreen(navController: NavController) {
    val list = mutableListOf<String>()
    for (i in 0..100) {
        list.add(i.toString())
    }
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {
        item {
            CustomItem("List Screen")
        }
        items(list) {
            CustomItem(it) {
                navController.navigate("details/$it")
            }
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController, itemId: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CustomItem("Detail Screen ($itemId)") {
            navController.navigate("profile")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {
        CustomItem("Profile Screen") {
            navController.navigate("social")
        }
    }
}

@Composable
fun SocialScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CustomItem("Social Screen")
    }
}

@Composable
fun CustomItem(title: String, onClick: () -> Unit = {}) {
    val actualTitle = remember { mutableStateOf(title) }
    Text(
        text = actualTitle.value,
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    )
}