package com.fragula2

import android.view.View
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import com.blacksquircle.fragula.R

class FragulaNavHostFragment : NavHostFragment() {

    private val navContainerId: Int
        get() = if (id != 0 && id != View.NO_ID) id else R.id.nav_host_fragment_container

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        navHostController.navigatorProvider +=
            SwipeBackNavigator(requireContext(), childFragmentManager, navContainerId)
    }
}