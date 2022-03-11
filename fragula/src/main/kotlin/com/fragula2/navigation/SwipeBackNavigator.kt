package com.fragula2.navigation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import com.fragula2.R
import com.fragula2.utils.toFragulaEntry

@Navigator.Name("swipeable")
class SwipeBackNavigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
) : Navigator<SwipeBackNavigator.Destination>() {

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
        val hasFragments = fragmentManager.fragments.isNotEmpty()
        if (initialNavigation) {
            val swipeBackFragment = SwipeBackFragment()
            fragmentManager.beginTransaction().apply {
                replace(containerId, swipeBackFragment, FRAGMENT_TAG)
                setPrimaryNavigationFragment(swipeBackFragment)
                if (!initialNavigation || hasFragments) {
                    addToBackStack(entry.id)
                }
                setReorderingAllowed(true)
                commit()
            }
        }
        val swipeBackFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (swipeBackFragment is Navigable) {
            swipeBackFragment.navigate(entry.toFragulaEntry())
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
            if (swipeBackFragment is Navigable) {
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

    override fun createDestination(): Destination {
        return Destination(this)
    }

    override fun onSaveState(): Bundle? = null
    override fun onRestoreState(savedState: Bundle) = Unit

    @NavDestination.ClassType(Fragment::class)
    class Destination constructor(
        swipeBackNavigator: Navigator<out Destination>,
    ) : NavDestination(swipeBackNavigator) {

        private var _className: String? = null
        val className: String
            get() {
                checkNotNull(_className) { "Fragment class was not set" }
                return _className as String
            }

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            context.resources.obtainAttributes(attrs, R.styleable.SwipeBackNavigator).use { array ->
                val className = array.getString(R.styleable.SwipeBackNavigator_android_name)
                if (className != null) {
                    _className = className
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Destination) return false
            return super.equals(other) && _className == other._className
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + _className.hashCode()
            return result
        }
    }

    private companion object {
        private const val TAG = "SwipeBackNavigator"
        private const val FRAGMENT_TAG = "SwipeBackFragment"
    }
}