package com.fragula2

import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign

class FragulaNavHostFragment : NavHostFragment() {

    private val navContainerId: Int
        get() {
            val method = javaClass.superclass.getDeclaredMethod("getContainerId")
            method.isAccessible = true
            return method.invoke(this) as Int
        }

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        navController.navigatorProvider +=
            SwipeBackNavigator(requireContext(), childFragmentManager, navContainerId)
    }
}