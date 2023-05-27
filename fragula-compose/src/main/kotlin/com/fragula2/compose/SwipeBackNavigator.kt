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

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberFragulaNavController(
    vararg navigators: Navigator<out NavDestination>,
): NavHostController {
    val swipeBackNavigator = remember { SwipeBackNavigator() }
    return rememberNavController(swipeBackNavigator, *navigators)
}

@Navigator.Name("swipeable")
class SwipeBackNavigator : Navigator<SwipeBackNavigator.Destination>() {

    internal val transitionsInProgress get() = state.transitionsInProgress
    internal val backStack get() = state.backStack

    internal var backTo by mutableStateOf<BackTo?>(null)
    internal var slideOut = false

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?,
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun createDestination(): Destination {
        return Destination(this) { }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        when {
            slideOut -> {
                state.popWithTransition(
                    popUpTo = popUpTo,
                    saveState = savedState,
                )
                slideOut = false
            }
            backTo == null -> {
                backTo = BackTo(popUpTo, savedState)
            }
            else -> Unit // ignore back button
        }
    }

    internal fun markTransitionComplete(entry: NavBackStackEntry) {
        if (backTo != null) {
            state.popWithTransition(
                popUpTo = backTo?.popUpTo ?: return,
                saveState = backTo?.saveState ?: return,
            )
            backTo = null
        }
        state.markTransitionComplete(entry)
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: SwipeBackNavigator,
        internal val content: @Composable (NavBackStackEntry) -> Unit,
    ) : NavDestination(navigator)

    companion object {
        internal const val NAME = "swipeable"
    }
}