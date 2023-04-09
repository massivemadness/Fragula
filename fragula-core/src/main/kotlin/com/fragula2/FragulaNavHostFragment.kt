package com.fragula2

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import com.fragula2.animation.SwipeController
import com.fragula2.common.SwipeDirection
import com.fragula2.navigation.SwipeBackNavigator
import androidx.navigation.fragment.R as NavR

class FragulaNavHostFragment : NavHostFragment() {

    private val containerId: Int
        get() = if (id != 0 && id != View.NO_ID) id else NavR.id.nav_host_fragment_container

    private var swipeDirection = SwipeDirection.LEFT_TO_RIGHT

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        context.obtainStyledAttributes(attrs, R.styleable.FragulaNavHostFragment).use { array ->
            swipeDirection = SwipeDirection.of(
                array.getInteger(R.styleable.FragulaNavHostFragment_swipeDirection, 0),
            )
        }
    }

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        navHostController.navigatorProvider += SwipeBackNavigator(
            fragmentManager = childFragmentManager,
            swipeDirection = swipeDirection,
            fragmentTag = FRAGMENT_TAG,
            containerId = containerId,
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