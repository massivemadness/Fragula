package com.fragula2.compose

import android.animation.TimeInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.compose.DialogHost
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.LocalOwnersProvider
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun FragulaNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    FragulaNavHost(
        navController.apply {
            navigatorProvider.addNavigator(SwipeBackNavigator())
        },
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier
    )
}

fun NavGraphBuilder.swipeable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        SwipeBackNavigator.Destination(provider[SwipeBackNavigator::class], content).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalPagerApi::class)
fun FragulaNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "FragulaNavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    // Setup the navController with proper owners
    navController.setLifecycleOwner(lifecycleOwner)
    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
    if (onBackPressedDispatcher != null) {
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }

    // Ensure that the NavController only receives back events while
    // the NavHost is in composition
    DisposableEffect(navController) {
        navController.enableOnBackPressed(true)
        onDispose {
            navController.enableOnBackPressed(false)
        }
    }

    // Then set the graph
    navController.graph = graph

    // Find the SwipeBackNavigator, returning early if it isn't found
    // (such as is the case when using TestNavHostController)
    val swipeBackNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>(SwipeBackNavigator.NAME) as? SwipeBackNavigator
        ?: return

    val backStack by swipeBackNavigator.backStack.collectAsState()
    val saveableStateHolder = rememberSaveableStateHolder()
    val pagerState = rememberPagerState()

    val scrollToEnd = remember { mutableStateOf(false) }
    val scrollOffset = remember { mutableStateOf(0f) }

    val renderElevation = remember { mutableStateOf(false) }
    val renderOffset = remember { mutableStateOf(0f) }

    HorizontalPager(
        count = backStack.size,
        key = { backStack[it].id },
        state = pagerState,
    ) { page ->
        val entry = backStack[page]
        val destination = entry.destination as SwipeBackNavigator.Destination

        scrollToEnd.value = currentPage + currentPageOffset < scrollOffset.value
        scrollOffset.value = currentPage + currentPageOffset

        renderOffset.value = if (currentPageOffset < 0) 1 + currentPageOffset else currentPageOffset
        renderElevation.value = renderOffset.value > 0

        Box {
            entry.LocalOwnersProvider(saveableStateHolder) {
                destination.content(entry)
            }
        }
        LaunchedEffect(entry) {
            launch {
                val pageIndex = backStack.indexOfFirst { it.id == entry.id }
                if (pageIndex > -1) {
                    pagerState.animateScrollToPage(
                        page = pageIndex,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = DecelerateInterpolator(1.78f).toEasing()
                        )
                    )
                }
            }
        }
    }

    if (renderElevation.value) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            val offsetX = -(renderOffset.value * maxWidth.value).dp
            Box(modifier = Modifier.fillMaxHeight()
                .requiredWidth(3.dp)
                .offset(x = offsetX)
                .background(Color.Black)
            )
        }
    }

    val dialogNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>("dialog") as? DialogNavigator // DialogNavigator.NAME
        ?: return

    // Show any dialog destinations
    DialogHost(dialogNavigator)
}

private fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }