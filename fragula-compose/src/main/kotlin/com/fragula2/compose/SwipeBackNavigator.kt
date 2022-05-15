package com.fragula2.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("swipeable")
class SwipeBackNavigator : Navigator<SwipeBackNavigator.Destination>() {

    internal val transitionsInProgress get() = state.transitionsInProgress

    internal val backStack get() = state.backStack

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun createDestination(): Destination {
        return Destination(this) { }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
    }

    internal fun onTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: SwipeBackNavigator,
        internal val content: @Composable (NavBackStackEntry) -> Unit
    ) : NavDestination(navigator)

    companion object {
        internal const val NAME = "swipeable"
    }
}