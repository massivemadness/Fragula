package com.fragula2.navigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.fragula2.common.SwipeDirection

@Navigator.Name("swipeable")
class SwipeBackNavigator(
    private val fragmentManager: FragmentManager,
    private val swipeDirection: SwipeDirection,
    private val fragmentTag: String,
    private val containerId: Int,
) : Navigator<SwipeBackDestination>() {

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
        if (initialNavigation) {
            fragmentManager.commit {
                val swipeBackFragment = SwipeBackFragment.newInstance(swipeDirection)
                replace(containerId, swipeBackFragment, fragmentTag)
                setPrimaryNavigationFragment(swipeBackFragment)
                if (!initialNavigation) {
                    addToBackStack(entry.id)
                }
                setReorderingAllowed(true)
            }
        }
        val swipeBackFragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (swipeBackFragment is Navigable) {
            swipeBackFragment.navigate(entry)
        }
        state.push(entry)
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return
        }
        if (backStack.size > 1) {
            val swipeBackFragment = fragmentManager.findFragmentByTag(fragmentTag)
            if (swipeBackFragment is Navigable) {
                swipeBackFragment.popBackStack(popUpTo)
            }
        } else {
            fragmentManager.popBackStack(
                popUpTo.id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        state.pop(popUpTo, savedState)
    }

    override fun createDestination(): SwipeBackDestination {
        return SwipeBackDestination(this)
    }

    override fun onSaveState(): Bundle? = null
    override fun onRestoreState(savedState: Bundle) = Unit

    companion object {
        private const val TAG = "SwipeBackNavigator"
    }
}