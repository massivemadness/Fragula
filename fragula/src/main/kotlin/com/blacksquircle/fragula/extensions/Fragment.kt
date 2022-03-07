package com.blacksquircle.fragula.extensions

import android.view.ViewParent
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blacksquircle.fragula.Navigator
import com.blacksquircle.fragula.common.BundleBuilder

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
inline fun <reified T : Fragment> Fragment.addFragment(
    @IdRes navigatorId: Int? = null,
    noinline bundleBuilder: (BundleBuilder.() -> Unit)? = null
) {

    val fragment = T::class.java.newInstance()

    val navigator = if (navigatorId == null) {
        this.parentNavigator
    } else {
        val activity = activity
            ?: throw NullPointerException("The fragment is not yet attached to the Activity.")
        activity.findNavigatorView(navigatorId)
    }
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
inline fun <reified T : Fragment> Fragment.replaceFragment(
    position: Int? = null,
    @IdRes navigatorId: Int? = null,
    noinline bundleBuilder: (BundleBuilder.() -> Unit)? = null
) {

    val fragment = T::class.java.newInstance()

    val navigator = if (navigatorId == null) {
        this.parentNavigator
    } else {
        val activity = activity
            ?: throw NullPointerException("The fragment is not yet attached to the Activity.")
        activity.findNavigatorView(navigatorId)
    }
    navigator.replaceFragment(
        fragment = fragment,
        position = position,
        builder = bundleBuilder
    )
}

/**
 * Finds the interface implementation among the stack of fragments in the Navigator.
 */
inline fun <reified T> Fragment.getCallback(@IdRes navigatorId: Int? = null): T {
    val navigator = if (navigatorId == null) {
        this.parentNavigator
    } else {
        val activity = activity
            ?: throw NullPointerException("The fragment is not yet attached to the Activity.")
        activity.findNavigatorView(navigatorId)
    }

    val clazz = T::class.java

    val fragments = navigator.fragments
    for (i in fragments.lastIndex downTo 0) {
        val fragment = fragments[i]
        if (clazz.isInstance(fragment)) return fragment as T
    }

    val activity = navigator.context as? FragmentActivity
    if (clazz.isInstance(activity)) return activity as T

    throw IllegalStateException("Couldn't find a callback implementation in a Fragment or Activity.")
}

/**
 * Returns an instance of the Navigator inside the fragment.
 */
val Fragment.parentNavigator: Navigator
    get() {
        if (view == null) {
            throw NullPointerException(
                "View of the fragment is null. " +
                        "Maybe the view hasn't been created yet? Make sure that " +
                        "you call this method after creating the view."
            )
        }
        val navigator = checkParent(view!!.parent)
        return navigator as Navigator
    }

private fun checkParent(viewParent: ViewParent?): ViewParent? {
    return if (viewParent != null && viewParent is Navigator) return viewParent
    else if (viewParent != null && viewParent !is Navigator) checkParent(viewParent.parent)
    else throw IllegalStateException("The fragment is not attached to the Navigator.")
}