package com.fragula2.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.compose.DialogHost
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.LocalOwnersProvider

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
fun FragulaNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier = Modifier
) {

    // region SETUP

    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "FragulaNavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    navController.setLifecycleOwner(lifecycleOwner)
    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
    if (onBackPressedDispatcher != null) {
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }

    DisposableEffect(navController) {
        navController.enableOnBackPressed(true)
        onDispose {
            navController.enableOnBackPressed(false)
        }
    }

    navController.graph = graph

    val swipeBackNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>(SwipeBackNavigator.NAME) as? SwipeBackNavigator
        ?: return

    val backStack by swipeBackNavigator.backStack.collectAsState()
    val saveableStateHolder = rememberSaveableStateHolder()

    // endregion

    for (entry in backStack) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            var swipeState by rememberSwipeState()
            var scrollPosition by remember { mutableStateOf(0f) }
            val animatedScrollPosition by animateFloatAsState(
                targetValue = when (swipeState) {
                    SwipeState.MOVE_TO_START -> 0f
                    SwipeState.MOVE_TO_END -> maxWidth.value
                    SwipeState.FOLLOW_POINTER -> scrollPosition
                },
                animationSpec = tween(
                    durationMillis = if (swipeState != SwipeState.FOLLOW_POINTER) 500 else 0,
                    easing = SwipeInterpolator().toEasing()
                )
            ) { value ->
                when (value) {
                    0f -> {
                        scrollPosition = 0f
                        swipeState = SwipeState.FOLLOW_POINTER
                    }
                    maxWidth.value -> {
                        scrollPosition = 0f
                        swipeState = SwipeState.FOLLOW_POINTER
                        navController.popBackStack()
                    }
                }
            }

            Box(modifier = modifier.animateDrag(
                containerWidth = maxWidth.value,
                enabled = entry.id != backStack[0].id,
                onScrollChanged = { scrollPosition = it },
                onScrollCancelled = {
                    swipeState = if (scrollPosition > maxWidth.value / 2) {
                        SwipeState.MOVE_TO_END
                    } else {
                        SwipeState.MOVE_TO_START
                    }
                },
            ).offset(x = animatedScrollPosition.dp)) {
                val destination = entry.destination as SwipeBackNavigator.Destination
                entry.LocalOwnersProvider(saveableStateHolder) {
                    destination.content(entry)
                }
            }
            if (animatedScrollPosition > 0f) {
                val elevationWidth = 3.dp
                Box(modifier = Modifier.fillMaxHeight()
                    .requiredWidth(elevationWidth)
                    .offset(x = animatedScrollPosition.dp - elevationWidth)
                    .background(brush = Brush.horizontalGradient(
                        colors = listOf(ElevationEnd, ElevationStart)
                    ))
                )
            }
        }
    }

    // region DIALOGS

    val dialogNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>("dialog") as? DialogNavigator // DialogNavigator.NAME
        ?: return

    DialogHost(dialogNavigator)

    // endregion
}