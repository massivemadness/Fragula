package com.fragula.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.fragula.Navigator
import com.fragula.common.BundleBuilder

/**
 * Adds a [Fragment] to the stack of fragments of the [Navigator].
 * Example:
 * addFragment<BlankFragment>()
 *
 *
 * @param bundleBuilder Can be used with passing arguments using [BundleBuilder].
 * Example:
 * addFragment<BlankFragment> {
 *       "ARGUMENT_KEY_1" to "Example string"
 *       "ARGUMENTS_KEY_2" to 12345
 *  }
 *
 *
 * @param navigatorId can add a fragment to another [Navigator] if there are several of them
 * at the id of the Navigator.
 * Example:
 * addFragment<BlankFragment>(navigatorId = R.id.navigator2)
 */
inline fun <reified T : Fragment> Activity.addFragment(
    @IdRes navigatorId: Int? = null,
    noinline bundleBuilder: (BundleBuilder.() -> Unit)? = null
) {

    val fragment = T::class.java.newInstance()
    val navigator = this.findNavigatorView(navigatorId)

    navigator.addFragment(
        fragment = fragment,
        builder = bundleBuilder
    )
}

/**
 * Replaces the current or position-selected fragment with a new one.
 *
 * Example:
 * replaceFragment<BlankFragment>()
 *
 * @param position
 * replaceFragment<BlankFragment>(position = targetPosition)
 *
 * @param bundleBuilder
 * Can be used with passing arguments using [BundleBuilder].
 * Example:
 * replaceFragment<BlankFragment>(position = targetPosition) {
 *       "ARGUMENT_KEY_1" to "Example string"
 *       "ARGUMENTS_KEY_2" to 12345
 *  }
 *
 * @param navigatorId
 * You can also replace a fragment to another [Navigator] if there are several of them
 * at the id of the Navigator.
 * Example:
 * replaceFragment<BlankFragment>(navigatorId = R.id.navigator2)
 */
inline fun <reified T : Fragment> Activity.replaceFragment(
    position: Int? = null,
    @IdRes navigatorId: Int? = null,
    noinline bundleBuilder: (BundleBuilder.() -> Unit)? = null
) {

    val fragment = T::class.java.newInstance()
    val navigator = findNavigatorView(navigatorId)

    navigator.replaceFragment(
        fragment = fragment,
        position = position,
        builder = bundleBuilder
    )
}

/**
 *  Finds the Navigator in the activity.
 */
fun Activity.findNavigatorView(@IdRes navigatorId: Int? = null): Navigator {
    val decorView = (window.decorView as ViewGroup)

    val visited: MutableList<View> = mutableListOf()
    val unvisited: MutableList<View> = mutableListOf()
    unvisited.add(decorView)

    while (unvisited.isNotEmpty()) {
        val child = unvisited.removeAt(0)
        if (child is Navigator && (navigatorId == null || navigatorId == child.id)) return child
        visited.add(child)
        if (child !is ViewGroup) continue
        val childCount = child.childCount
        for (i in 0 until childCount) unvisited.add(child.getChildAt(i))
    }

    throw NullPointerException("Activity doesn't have a Navigator. Add a Navigator view to the xml layout of your Activity.")
}