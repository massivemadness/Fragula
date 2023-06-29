/*
 * Copyright 2023 Fragula contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fragula2

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import com.fragula2.animation.SwipeController
import com.fragula2.common.SwipeDirection
import com.fragula2.navigation.SwipeBackFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Set [androidx.activity.OnBackPressedDispatcher] for this NavController to handle
         * system back button, as the back button handling was delegated to the FragmentManager
         * in androidx.navigation 2.6.0
         */
        val onBackPressedDispatcher = (context as OnBackPressedDispatcherOwner).onBackPressedDispatcher
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        navHostController.navigatorProvider += SwipeBackNavigator(
            fragmentManager = childFragmentManager,
            swipeDirection = swipeDirection,
            containerId = containerId,
        )
    }

    companion object {

        @JvmStatic
        fun findSwipeController(fragment: Fragment): SwipeController {
            var findFragment: Fragment? = fragment
            while (findFragment != null) {
                if (findFragment is FragulaNavHostFragment) {
                    val fragmentManager = findFragment.childFragmentManager
                    return fragmentManager.findFragmentByTag(SwipeBackFragment.TAG) as SwipeController
                }
                val primaryNavFragment = findFragment.parentFragmentManager
                    .primaryNavigationFragment
                if (primaryNavFragment is FragulaNavHostFragment) {
                    val fragmentManager = primaryNavFragment.childFragmentManager
                    return fragmentManager.findFragmentByTag(SwipeBackFragment.TAG) as SwipeController
                }
                findFragment = findFragment.parentFragment
            }
            throw IllegalStateException("Fragment $fragment does not have a SwipeController set")
        }
    }
}