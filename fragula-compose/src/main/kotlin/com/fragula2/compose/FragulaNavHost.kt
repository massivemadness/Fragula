package com.fragula2.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
        navController = navController.apply {
            navigatorProvider.addNavigator(SwipeBackNavigator())
        },
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

    val saveableStateHolder = rememberSaveableStateHolder()
    val backStack by swipeBackNavigator.backStack.collectAsState()
    val transitionsInProgress by swipeBackNavigator.transitionsInProgress.collectAsState()

    // endregion

    var initialAnimation by remember { mutableStateOf(true) }
    var parallaxEffect by remember { mutableStateOf(0f) }
    for (backStackEntry in backStack) { // FIXME don't render all entries at once
        var dimmingEffect by remember { mutableStateOf(0f) }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .background(dimColor.copy(alpha = dimmingEffect))
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
                    SwipeState.FOLLOW_POINTER -> pointerPosition
                    SwipeState.SWIPE_IN -> startPosition
                    SwipeState.SWIPE_OUT -> endPosition
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

            val applyDraggable = backStackEntry.id != backStack.firstOrNull()?.id
            val applyParallax = backStackEntry.id == backStack.penultOrNull()?.id
            val calculateEffects = backStackEntry.id == backStack.lastOrNull()?.id

            if (calculateEffects) {
                val progress = scrollPosition / (endPosition * 0.01f)
                val scrollOffset = progress * 0.01f // range 0f..1f
                parallaxEffect = -maxWidth.value * (1.0f - scrollOffset) / parallaxFactor
                dimmingEffect = (1.0f - scrollOffset) * dimAmount
            }

            DisposableEffect(backStackEntry) {
                if (initialAnimation) {
                    initialAnimation = false // FIXME animation on recomposition
                } else {
                    swipeState = SwipeState.SWIPE_IN
                }
                onDispose {
                    // TODO pop transition
                    // swipeState = SwipeState.SWIPE_OUT
                }
            }

            Box(modifier = modifier.animateDrag(
                enabled = applyDraggable,
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
                            pointerPosition > endPosition / 2 -> SwipeState.SWIPE_OUT
                            pointerPosition < endPosition / 2 -> SwipeState.SWIPE_IN
                            else -> SwipeState.FOLLOW_POINTER
                        }
                    }
                },
            ).graphicsLayer {
                translationX = if (applyParallax) parallaxEffect else scrollPosition
            }) {
                val destination = backStackEntry.destination as SwipeBackNavigator.Destination
                backStackEntry.LocalOwnersProvider(saveableStateHolder) {
                    destination.content(backStackEntry)
                }
            }
            if (scrollPosition > startPosition) {
                Canvas(modifier = Modifier.fillMaxHeight()
                    .requiredWidth(elevation)
                    .graphicsLayer {
                        translationX = scrollPosition - elevation.toPx()
                    }
                ) {
                    drawRect(brush = Brush.horizontalGradient(
                        colors = listOf(ElevationEnd, ElevationStart)
                    ))
                }
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