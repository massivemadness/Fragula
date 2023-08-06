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

package com.fragula2.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.DialogHost
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.createGraph
import androidx.navigation.get
import com.fragula2.common.SwipeDirection
import com.fragula2.common.SwipeInterpolator
import kotlin.math.abs

@Composable
fun FragulaNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    onPageScrolled: (Int, Float, Int) -> Unit = { _, _, _ -> },
    swipeDirection: SwipeDirection = SwipeDirection.LEFT_TO_RIGHT,
    scrollable: Boolean = true,
    scrimColor: Color = ScrimColor,
    scrimAmount: Float = 0.15f,
    elevationAmount: Dp = 3.dp,
    parallaxFactor: Float = 1.3f,
    animDurationMs: Int = 500,
    builder: NavGraphBuilder.() -> Unit,
) {
    FragulaNavHost(
        navController = navController,
        graph = remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier = modifier,
        onPageScrolled = onPageScrolled,
        swipeDirection = swipeDirection,
        scrollable = scrollable,
        scrimColor = scrimColor,
        elevationAmount = elevationAmount,
        scrimAmount = scrimAmount,
        parallaxFactor = parallaxFactor,
        animDurationMs = animDurationMs,
    )
}

fun NavGraphBuilder.swipeable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
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
        },
    )
}

@Composable
fun FragulaNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier,
    onPageScrolled: (Int, Float, Int) -> Unit,
    swipeDirection: SwipeDirection,
    scrollable: Boolean,
    scrimColor: Color,
    scrimAmount: Float,
    elevationAmount: Dp,
    parallaxFactor: Float,
    animDurationMs: Int,
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

    var parallaxOffset by remember { mutableStateOf(0f) }

    for ((index, backStackEntry) in backStack.withIndex()) { // FIXME don't render all entries at once
        SwipeableBox(
            navController = navController,
            swipeDirection = swipeDirection,
            position = index,
            pageCount = backStack.size,
            scrollable = scrollable,
            scrimColor = scrimColor,
            scrimAmount = scrimAmount,
            parallaxFactor = parallaxFactor,
            animDurationMs = animDurationMs,
            elevationAmount = elevationAmount,
            offsetProvider = { parallaxOffset },
            backToProvider = { swipeBackNavigator.backTo != null },
            positionChanger = { position, positionOffset, positionOffsetPixels ->
                onPageScrolled(position, positionOffset, positionOffsetPixels)
                parallaxOffset = positionOffset
            },
            onDragFinished = { swipeBackNavigator.slideOut = it == SwipeState.SLIDE_OUT },
            onScrollFinished = { swipeBackNavigator.markTransitionComplete(backStackEntry) },
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
    swipeDirection: SwipeDirection,
    position: Int,
    pageCount: Int,
    scrollable: Boolean,
    scrimColor: Color,
    scrimAmount: Float,
    parallaxFactor: Float,
    animDurationMs: Int,
    elevationAmount: Dp,
    offsetProvider: () -> Float,
    backToProvider: () -> Boolean,
    positionChanger: (Int, Float, Int) -> Unit,
    onDragFinished: (SwipeState) -> Unit,
    onScrollFinished: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        val pageStart = 0f
        val pageEnd = if (swipeDirection.isHorizontal()) {
            constraints.maxWidth.toFloat()
        } else {
            constraints.maxHeight.toFloat()
        }
        val parallaxFormula = {
            if (swipeDirection.isRTL()) {
                maxHeight.value * (1.0f - offsetProvider()) / parallaxFactor
            } else {
                -maxWidth.value * (1.0f - offsetProvider()) / parallaxFactor
            }
        }

        var swipeState by rememberSaveable { mutableStateOf(SwipeState.FOLLOW_POINTER) }
        var animateSlideIn by rememberSaveable { mutableStateOf(position > 0) }
        var pointerPosition by remember {
            val initialValue = if (animateSlideIn) pageEnd else pageStart
            mutableStateOf(initialValue)
        }
        val scrollPosition by animateFloatAsState(
            targetValue = when (swipeState) {
                SwipeState.FOLLOW_POINTER -> pointerPosition
                SwipeState.SLIDE_IN -> pageStart
                SwipeState.SLIDE_OUT -> pageEnd
            },
            animationSpec = tween(
                durationMillis = if (swipeState != SwipeState.FOLLOW_POINTER) animDurationMs else 0,
                easing = SwipeInterpolator().toEasing(),
            ),
        ) { value ->
            when (value) {
                pageStart -> {
                    pointerPosition = pageStart
                    swipeState = SwipeState.FOLLOW_POINTER
                    onScrollFinished()
                }

                pageEnd -> {
                    pointerPosition = pageStart
                    swipeState = SwipeState.FOLLOW_POINTER
                    navController.popBackStack()
                    onScrollFinished()
                }
            }
        }

        val applyParallax = position == pageCount - 2
        val calculatePosition = position == pageCount - 1
        if (calculatePosition) {
            val progress = scrollPosition / (pageEnd * 0.01f)
            val positionOffset = progress * 0.01f // range 0f..1f
            val positionOffsetPixels = (pageEnd * (1.0f - positionOffset)).toInt()
            val positionActual = if (scrollPosition > pageStart) position - 1 else position
            positionChanger(positionActual, positionOffset, positionOffsetPixels)

            if (backToProvider()) { // popBackStack() was called
                swipeState = SwipeState.SLIDE_OUT
            }
        }

        LaunchedEffect(position) {
            if (animateSlideIn) {
                animateSlideIn = false
                swipeState = SwipeState.SLIDE_IN
            }
        }

        val applyScrim by remember {
            derivedStateOf { scrollPosition > pageStart && scrollPosition < pageEnd }
        }
        if (applyScrim) {
            PageScrim(
                positionProvider = { scrollPosition },
                pageEnd = pageEnd,
                scrimColor = scrimColor,
                scrimAmount = scrimAmount,
            )
        }

        Box(
            modifier = modifier
                .animateDrag(
                    enabled = position > 0 && scrollable,
                    swipeDirection = swipeDirection,
                    onDragChanged = { position ->
                        if (swipeState == SwipeState.FOLLOW_POINTER) {
                            pointerPosition = abs(position)
                            if (position > (pageEnd + elevationAmount.value) ||
                                position < (-pageEnd + -elevationAmount.value)
                            ) {
                                swipeState = SwipeState.SLIDE_OUT
                            }
                        }
                    },
                    onDragFinished = { velocity ->
                        if (swipeState == SwipeState.FOLLOW_POINTER) {
                            swipeState = when {
                                velocity > 1000 -> SwipeState.SLIDE_OUT // Fling
                                pointerPosition == 0f -> SwipeState.FOLLOW_POINTER
                                pointerPosition > pageEnd / 2 -> SwipeState.SLIDE_OUT
                                pointerPosition < pageEnd / 2 -> SwipeState.SLIDE_IN
                                else -> SwipeState.FOLLOW_POINTER
                            }
                            onDragFinished(swipeState)
                        }
                    },
                )
                .graphicsLayer {
                    val translation = if (swipeDirection.isRTL()) {
                        -scrollPosition
                    } else {
                        scrollPosition
                    }
                    if (swipeDirection.isHorizontal()) {
                        translationX = if (applyParallax) parallaxFormula() else translation
                    } else {
                        translationY = if (applyParallax) parallaxFormula() else translation
                    }
                },
        ) {
            content()
        }

        val applyElevation by remember {
            derivedStateOf { scrollPosition > pageStart && scrollPosition < pageEnd }
        }
        if (applyElevation) {
            PageElevation(
                positionProvider = { scrollPosition },
                pageEnd = pageEnd,
                swipeDirection = swipeDirection,
                elevationAmount = elevationAmount,
            )
        }
    }
}

@Composable
private fun PageScrim(
    positionProvider: () -> Float,
    pageEnd: Float,
    scrimColor: Color,
    scrimAmount: Float,
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val progress = positionProvider() / (pageEnd * 0.01f)
                val scrollOffset = progress * 0.01f // range 0f..1f
                alpha = (1.0f - scrollOffset) * scrimAmount
            },
    ) {
        drawRect(color = scrimColor)
    }
}

@Composable
private fun PageElevation(
    positionProvider: () -> Float,
    pageEnd: Float,
    swipeDirection: SwipeDirection,
    elevationAmount: Dp,
) {
    Canvas(
        modifier = Modifier
            .then(
                if (swipeDirection.isHorizontal()) {
                    Modifier
                        .fillMaxHeight()
                        .requiredWidth(elevationAmount)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .requiredHeight(elevationAmount)
                },
            )
            .graphicsLayer {
                val translation = if (swipeDirection.isRTL()) {
                    pageEnd - positionProvider()
                } else {
                    positionProvider()
                }
                if (swipeDirection.isHorizontal()) {
                    translationX = translation - elevationAmount.toPx()
                } else {
                    translationY = translation - elevationAmount.toPx()
                }
            },
    ) {
        val colors = if (swipeDirection.isRTL()) {
            listOf(ElevationEnd, ElevationStart)
        } else {
            listOf(ElevationStart, ElevationEnd)
        }
        val brush = if (swipeDirection.isHorizontal()) {
            Brush.horizontalGradient(
                colors = colors,
            )
        } else {
            Brush.verticalGradient(
                colors = colors,
            )
        }
        drawRect(
            brush = brush,
        )
    }
}

@Composable
private fun NavHostContent(
    saveableStateHolder: SaveableStateHolder,
    backStackEntry: NavBackStackEntry,
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