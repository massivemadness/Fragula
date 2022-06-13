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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
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
    dimColor: Color = DimColor,
    dimAmount: Float = 0.1f,
    animDurationMs: Int = 500,
    elevationDp: Dp = 3.dp,
    builder: NavGraphBuilder.() -> Unit
) {
    FragulaNavHost(
        navController = navController.apply {
            navigatorProvider.addNavigator(SwipeBackNavigator())
        },
        graph = remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier = modifier,
        dimColor = dimColor,
        dimAmount = dimAmount,
        animDurationMs = animDurationMs,
        elevationDp = elevationDp
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
internal fun FragulaNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier,
    dimColor: Color,
    dimAmount: Float,
    animDurationMs: Int,
    elevationDp: Dp,
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
    val transitionsInProgress by swipeBackNavigator.transitionsInProgress.collectAsState()
    val saveableStateHolder = rememberSaveableStateHolder()

    // endregion

    var initialAnimation by remember { mutableStateOf(true) }
    for (entry in backStack) {
        var scrollOffset by remember { mutableStateOf(1f) }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .background(dimColor.copy(alpha = (1.0f - scrollOffset) * dimAmount))
        ) {
            val startPosition = 0f
            val endPosition = constraints.maxWidth.toFloat()

            var swipeState by rememberSwipeState()
            var pointerPosition by remember {
                val initialValue = if (initialAnimation) startPosition else endPosition
                mutableStateOf(initialValue)
            }
            val scrollPosition by animateFloatAsState(
                targetValue = when (swipeState) {
                    SwipeState.MOVE_TO_START -> startPosition
                    SwipeState.MOVE_TO_END -> endPosition
                    SwipeState.FOLLOW_POINTER -> pointerPosition
                },
                animationSpec = tween(
                    durationMillis = if (swipeState != SwipeState.FOLLOW_POINTER) animDurationMs else 0,
                    easing = SwipeInterpolator().toEasing()
                )
            ) { value ->
                when (value) {
                    startPosition -> {
                        pointerPosition = startPosition
                        swipeState = SwipeState.FOLLOW_POINTER
                        transitionsInProgress.forEach { entry ->
                            swipeBackNavigator.onTransitionComplete(entry)
                        }
                    }
                    endPosition -> {
                        pointerPosition = startPosition
                        swipeState = SwipeState.FOLLOW_POINTER
                        transitionsInProgress.forEach { entry ->
                            swipeBackNavigator.onTransitionComplete(entry)
                        }
                        navController.popBackStack()
                    }
                }
            }

            val progress = scrollPosition / (endPosition * 0.01f)
            scrollOffset = progress * 0.01f

            DisposableEffect(entry) {
                if (initialAnimation) {
                    initialAnimation = false
                } else {
                    swipeState = SwipeState.MOVE_TO_START
                }
                onDispose {
                    swipeState = SwipeState.MOVE_TO_END // FIXME pop transition
                }
            }

            Box(modifier = modifier.animateDrag(
                enabled = entry.id != backStack[0].id,
                onScrollChanged = { pointerPosition = it },
                onScrollCancelled = {
                    swipeState = if (pointerPosition > endPosition / 2) {
                        SwipeState.MOVE_TO_END
                    } else {
                        SwipeState.MOVE_TO_START
                    }
                },
            ).graphicsLayer {
                translationX = scrollPosition
                // TODO parallax
            }) {
                val destination = entry.destination as SwipeBackNavigator.Destination
                entry.LocalOwnersProvider(saveableStateHolder) {
                    destination.content(entry)
                }
            }
            if (scrollPosition > startPosition) {
                Box(modifier = Modifier.fillMaxHeight()
                    .requiredWidth(elevationDp)
                    .graphicsLayer { translationX = scrollPosition - elevationDp.toPx() }
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