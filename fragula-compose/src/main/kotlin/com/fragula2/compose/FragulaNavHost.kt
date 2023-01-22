package com.fragula2.compose

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.fragula2.common.SwipeInterpolator

@Composable
fun FragulaNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    dimColor: Color = DimColor,
    dimAmount: Float = 0.1f,
    parallaxFactor: Float = 1.3f,
    animDurationMs: Int = 500,
    elevation: Dp = 3.dp,
    builder: NavGraphBuilder.() -> Unit
) {
    FragulaNavHost(
        navController = navController,
        graph = remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier = modifier,
        dimColor = dimColor,
        dimAmount = dimAmount,
        parallaxFactor = parallaxFactor,
        animDurationMs = animDurationMs,
        elevation = elevation
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
    modifier: Modifier,
    dimColor: Color,
    dimAmount: Float,
    parallaxFactor: Float,
    animDurationMs: Int,
    elevation: Dp,
) {

    // region SETUP

    NavHostLifecycle(navController)
    NavHostBackHandler(navController)

    navController.graph = graph

    val swipeBackNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>(SwipeBackNavigator.NAME) as? SwipeBackNavigator
        ?: return
    val backStack by swipeBackNavigator.backStack.collectAsState()
    val saveableStateHolder = rememberSaveableStateHolder()

    // endregion

    for ((index, backStackEntry) in backStack.withIndex()) { // FIXME don't render all entries at once
        SwipeableBox(
            navController = navController,
            backStackEntryIndex = index,
            animDurationMs = animDurationMs,
            elevation = elevation,
            modifier = modifier.fillMaxSize(),
        ) {
            NavHostContent(saveableStateHolder, backStackEntry)
        }
    }

    // region DIALOGS

    val dialogNavigator = navController.navigatorProvider
        .get<Navigator<out NavDestination>>("dialog") as? DialogNavigator // DialogNavigator.NAME
        ?: return

    DialogHost(dialogNavigator)

    // endregion
}

@Composable
private fun SwipeableBox(
    navController: NavHostController,
    backStackEntryIndex: Int,
    animDurationMs: Int,
    elevation: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        val pageStart = 0f
        val pageEnd = constraints.maxWidth.toFloat()

        var animateSlideIn by rememberSaveable { mutableStateOf(backStackEntryIndex > 0) }
        var swipeState by rememberSwipeState()
        var pointerPosition by rememberSaveable {
            val initialValue = if (animateSlideIn) pageEnd else pageStart
            mutableStateOf(initialValue)
        }
        val scrollPosition by animateFloatAsState(
            targetValue = when (swipeState) {
                SwipeState.FOLLOW_POINTER -> pointerPosition
                SwipeState.SWIPE_IN -> pageStart
                SwipeState.SWIPE_OUT -> pageEnd
            },
            animationSpec = tween(
                durationMillis = if (swipeState != SwipeState.FOLLOW_POINTER) animDurationMs else 0,
                easing = SwipeInterpolator().toEasing()
            )
        ) { value ->
            when (value) {
                pageStart -> {
                    pointerPosition = pageStart
                    swipeState = SwipeState.FOLLOW_POINTER
                }
                pageEnd -> {
                    pointerPosition = pageStart
                    swipeState = SwipeState.FOLLOW_POINTER
                    navController.popBackStack()
                }
            }
        }

        DisposableEffect(backStackEntryIndex) {
            if (animateSlideIn) {
                animateSlideIn = false
                swipeState = SwipeState.SWIPE_IN
            }
            onDispose {
                Log.d("FragulaNavHost", "NavBackStackEntry disposed")
            }
        }

        Box(modifier = modifier.animateDrag(
            onScrollChanged = { position ->
                if (swipeState == SwipeState.FOLLOW_POINTER) {
                    pointerPosition = position
                }
            },
            onScrollCancelled = { velocity ->
                if (swipeState == SwipeState.FOLLOW_POINTER) {
                    swipeState = when {
                        velocity > 1000 -> SwipeState.SWIPE_OUT
                        pointerPosition == 0f -> SwipeState.FOLLOW_POINTER
                        pointerPosition > pageEnd / 2 -> SwipeState.SWIPE_OUT
                        pointerPosition < pageEnd / 2 -> SwipeState.SWIPE_IN
                        else -> SwipeState.FOLLOW_POINTER
                    }
                }
            },
        ).graphicsLayer {
            translationX = scrollPosition
        }) {
            content()
        }

        val applyElevation by remember {
            derivedStateOf { scrollPosition > pageStart }
        }
        if (applyElevation) {
            PageElevation(
                positionProvider = { scrollPosition },
                elevation = elevation
            )
        }
    }
}

@Composable
private fun PageElevation(
    positionProvider: () -> Float,
    elevation: Dp,
) {
    Canvas(modifier = Modifier.fillMaxHeight()
        .requiredWidth(elevation)
        .graphicsLayer {
            translationX = positionProvider() - elevation.toPx()
        }
    ) {
        drawRect(brush = Brush.horizontalGradient(
            colors = listOf(ElevationEnd, ElevationStart)
        ))
    }
}

@Composable
private fun NavHostContent(
    saveableStateHolder: SaveableStateHolder,
    backStackEntry: NavBackStackEntry
) {
    val destination = backStackEntry.destination as SwipeBackNavigator.Destination
    backStackEntry.LocalOwnersProvider(saveableStateHolder) {
        destination.content(backStackEntry)
    }
}

@Composable
private fun NavHostLifecycle(navController: NavHostController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "FragulaNavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    navController.setLifecycleOwner(lifecycleOwner)
    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
}

@Composable
private fun NavHostBackHandler(navController: NavHostController) {
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher
    if (onBackPressedDispatcher != null) {
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }
    DisposableEffect(navController) {
        navController.enableOnBackPressed(true)
        onDispose {
            navController.enableOnBackPressed(false)
        }
    }
}