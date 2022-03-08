package com.fragula2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("swipeable")
internal class SwipeBackNavigator(
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
            navigate(entry)
        }
    }

    private fun navigate(entry: NavBackStackEntry) {
        val initialNavigation = backStack.isEmpty()
        val destination = entry.destination as FragmentNavigator.Destination
        val className = destination.className

        if (initialNavigation) {
            val swipeBackFragment = SwipeBackFragment.newInstance(className)
            fragmentManager.beginTransaction().apply {
                replace(containerId, swipeBackFragment, FRAGMENT_TAG)
                setPrimaryNavigationFragment(swipeBackFragment)
                if (!initialNavigation) {
                    addToBackStack(entry.id)
                }
                setReorderingAllowed(true)
                commit()
            }
        } else {
            val swipeBackFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (swipeBackFragment is SwipeBackFragment) {
                swipeBackFragment.navigate(className)
            }
        }
        state.push(entry)
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return
        }
        if (backStack.size > 1) {
            val swipeBackFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (swipeBackFragment is SwipeBackFragment) {
                swipeBackFragment.popBackStack()
            }
        } else {
            fragmentManager.popBackStack(
                popUpTo.id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
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