package com.fragula2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("swipeback")
class SwipeBackNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
) : Navigator<FragmentNavigator.Destination>() {

    private val savedIds = mutableSetOf<String>()

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
        val backStack = state.backStack.value
        val initialNavigation = backStack.isEmpty()
        val restoreState = navOptions != null &&
            !initialNavigation &&
            navOptions.shouldRestoreState() &&
            savedIds.remove(entry.id)

        if (restoreState) {
            fragmentManager.restoreBackStack(entry.id)
            state.push(entry)
            return
        }
        val destination = entry.destination as FragmentNavigator.Destination
        val args = entry.arguments
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }
        val frag = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
        frag.arguments = args
        val transaction = fragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        transaction.replace(containerId, frag)
        transaction.setPrimaryNavigationFragment(frag)

        val isSingleTopReplacement = (
            navOptions != null && !initialNavigation &&
                navOptions.shouldLaunchSingleTop() &&
                backStack.last().destination.id == destination.id
            )
        val isAdded = when {
            initialNavigation -> true
            isSingleTopReplacement -> {
                if (backStack.size > 1) {
                    fragmentManager.popBackStack(
                        entry.id,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    transaction.addToBackStack(entry.id)
                }
                false
            }
            else -> {
                transaction.addToBackStack(entry.id)
                true
            }
        }
        if (navigatorExtras is FragmentNavigator.Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                transaction.addSharedElement(key, value)
            }
        }
        transaction.setReorderingAllowed(true)
        transaction.commit()

        if (isAdded) {
            state.push(entry)
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return
        }
        if (savedState) {
            val beforePopList = state.backStack.value
            val initialEntry = beforePopList.first()
            val poppedList = beforePopList.subList(
                beforePopList.indexOf(popUpTo),
                beforePopList.size
            )
            for (entry in poppedList.reversed()) {
                if (entry == initialEntry) {
                    Log.i(TAG, "FragmentManager cannot save the state of the initial destination $entry")
                } else {
                    fragmentManager.saveBackStack(entry.id)
                    savedIds += entry.id
                }
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
        if (savedIds.isEmpty()) {
            return null
        }
        return bundleOf(KEY_SAVED_IDS to listOf(savedIds))
    }

    override fun onRestoreState(savedState: Bundle) {
        val savedIds = savedState.getStringArrayList(KEY_SAVED_IDS)
        if (savedIds != null) {
            this.savedIds.clear()
            this.savedIds += savedIds
        }
    }

    private companion object {
        private const val TAG = "SwipeBackNavigator"
        private const val KEY_SAVED_IDS = "androidx-nav-swipeback:navigator:savedIds"
    }
}