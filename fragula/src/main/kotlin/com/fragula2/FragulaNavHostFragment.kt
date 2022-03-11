package com.fragula2

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import com.fragula2.navigation.SwipeBackNavigator
import com.fragula2.utils.SwipeController

class FragulaNavHostFragment : NavHostFragment() {

    private val containerId: Int
        get() = if (id != 0 && id != View.NO_ID) id else R.id.nav_host_fragment_container

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        navHostController.navigatorProvider += SwipeBackNavigator(
            childFragmentManager,
            FRAGMENT_TAG,
            containerId
        )
    }

    companion object {

        private const val FRAGMENT_TAG = "SwipeBackFragment"

        @JvmStatic
        fun findSwipeController(fragment: Fragment): SwipeController {
            var findFragment: Fragment? = fragment
            while (findFragment != null) {
                if (findFragment is FragulaNavHostFragment) {
                    val fragmentManager = findFragment.childFragmentManager
                    return fragmentManager.findFragmentByTag(FRAGMENT_TAG) as SwipeController
                }
                val primaryNavFragment = findFragment.parentFragmentManager
                    .primaryNavigationFragment
                if (primaryNavFragment is FragulaNavHostFragment) {
                    val fragmentManager = primaryNavFragment.childFragmentManager
                    return fragmentManager.findFragmentByTag(FRAGMENT_TAG) as SwipeController
                }
                findFragment = findFragment.parentFragment
            }
            throw IllegalStateException("Fragment $fragment does not have a SwipeController set")
        }
    }
}