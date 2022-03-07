package com.fragula2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("swipeable")
class SwipeBackNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
) : Navigator<FragmentNavigator.Destination>() {

    private val backStack: List<NavBackStackEntry>
        get() = state.backStack.value

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return
        }
        for (entry in entries) {
            navigate(entry, navOptions, navigatorExtras)
        }
    }

    private fun navigate(
        entry: NavBackStackEntry,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        val initialNavigation = backStack.isEmpty()
        val destination = entry.destination as FragmentNavigator.Destination
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        if (initialNavigation) {
            val swipeBackFragment = SwipeBackFragment.newInstance(className)
            fragmentManager.beginTransaction()
                .replace(containerId, swipeBackFragment, FRAGMENT_TAG)
                .setPrimaryNavigationFragment(swipeBackFragment)
                .addToBackStack(entry.id)
                .setReorderingAllowed(true)
                .commit()
        } else {
            val swipeBackFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (swipeBackFragment is SwipeBackFragment) {
                swipeBackFragment.navigate(className)
            }
        }
        state.push(entry)

        // TODO arguments support
        // val args = entry.arguments
        // fragment.arguments = args

        // TODO animations support
        /*var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }*/

        // TODO shared transitions support
        /*if (navigatorExtras is FragmentNavigator.Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                transaction.addSharedElement(key, value)
            }
        }*/
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return
        }
        when {
            backStack.size > 1 -> {
                val swipeBackFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
                if (swipeBackFragment is SwipeBackFragment) {
                    swipeBackFragment.popBackStack()
                }
            }
            backStack.isEmpty() -> {
                fragmentManager.popBackStack(
                    popUpTo.id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
        state.pop(popUpTo, savedState)
    }

    override fun createDestination(): FragmentNavigator.Destination {
        return FragmentNavigator.Destination(this)
    }

    override fun onSaveState(): Bundle? {
        return null // FIXME do I really need this?
    }

    override fun onRestoreState(savedState: Bundle) {
        // FIXME do I really need this?
    }

    private companion object {
        private const val TAG = "SwipeBackNavigator"
        private const val FRAGMENT_TAG = "SwipeBackFragment"
    }
}