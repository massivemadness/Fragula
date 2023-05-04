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

@Composable
@Deprecated(
    message = "Replace rememberNavController with rememberFragulaNavController",
    replaceWith = ReplaceWith(
        "rememberFragulaNavController()",
        "com.fragula2.compose.rememberFragulaNavController",
    ),
)
fun rememberSwipeBackNavigator(): SwipeBackNavigator {
    return remember { SwipeBackNavigator() }
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
            backTo != null -> {
                state.popWithTransition(
                    popUpTo = backTo?.popUpTo ?: return,
                    saveState = backTo?.saveState ?: return,
                )
                backTo = null
            }
        }
    }

    internal fun markTransitionComplete(entry: NavBackStackEntry) {
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