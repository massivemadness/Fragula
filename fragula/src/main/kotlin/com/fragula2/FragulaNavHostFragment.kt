package com.fragula2

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.core.content.res.use
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.*
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.blacksquircle.fragula.R

class FragulaNavHostFragment : Fragment(), NavHost {

    override val navController: NavController
        get() {
            checkNotNull(navHostController) { "NavController is not available before onCreate()" }
            return navHostController as NavHostController
        }

    private val containerId: Int
        get() = if (id != 0 && id != View.NO_ID) id else R.id.fragula_nav_host_fragment_container

    private var navHostController: NavHostController? = null
    private var isPrimaryBeforeOnCreate: Boolean? = null
    private var viewParent: View? = null

    private var graphId = 0
    private var defaultNavHost = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (defaultNavHost) {
            parentFragmentManager.beginTransaction()
                .setPrimaryNavigationFragment(this)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var context = requireContext()
        navHostController = NavHostController(context)
        navHostController?.setLifecycleOwner(this)
        while (context is ContextWrapper) {
            if (context is OnBackPressedDispatcherOwner) {
                navHostController?.setOnBackPressedDispatcher(
                    (context as OnBackPressedDispatcherOwner).onBackPressedDispatcher
                )
                break
            }
            context = context.baseContext
        }
        navHostController?.enableOnBackPressed(
            isPrimaryBeforeOnCreate != null && isPrimaryBeforeOnCreate as Boolean
        )
        isPrimaryBeforeOnCreate = null
        navHostController?.setViewModelStore(viewModelStore)
        onCreateNavController(navHostController!!)
        var navState: Bundle? = null
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE)
            if (savedInstanceState.getBoolean(KEY_DEFAULT_NAV_HOST, false)) {
                defaultNavHost = true
                parentFragmentManager.beginTransaction()
                    .setPrimaryNavigationFragment(this)
                    .commit()
            }
            graphId = savedInstanceState.getInt(KEY_GRAPH_ID)
        }
        if (navState != null) {
            navHostController?.restoreState(navState)
        }
        if (graphId != 0) {
            navHostController?.setGraph(graphId)
        } else {
            val graphId = arguments?.getInt(KEY_GRAPH_ID) ?: 0
            val startDestinationArgs = arguments?.getBundle(KEY_START_DESTINATION_ARGS)
            if (graphId != 0) {
                navHostController?.setGraph(graphId, startDestinationArgs)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        if (navHostController != null) {
            navHostController?.enableOnBackPressed(isPrimaryNavigationFragment)
        } else {
            isPrimaryBeforeOnCreate = isPrimaryNavigationFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val containerView = FragmentContainerView(inflater.context)
        containerView.id = containerId
        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        check(view is ViewGroup) { "created host view $view is not a ViewGroup" }
        Navigation.setViewNavController(view, navHostController)
        if (view.getParent() != null) {
            viewParent = view.getParent() as View
            if (viewParent?.id == id) {
                Navigation.setViewNavController(viewParent!!, navHostController)
            }
        }
    }

    @SuppressLint("PrivateResource")
    override fun onInflate(
        context: Context,
        attrs: AttributeSet,
        savedInstanceState: Bundle?
    ) {
        super.onInflate(context, attrs, savedInstanceState)
        context.obtainStyledAttributes(attrs, R.styleable.NavHost).use { navHost ->
            val graphId = navHost.getResourceId(androidx.navigation.R.styleable.NavHost_navGraph, 0)
            if (graphId != 0) {
                this.graphId = graphId
            }
        }
        context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment).use { array ->
            val defaultNavHost = array.getBoolean(R.styleable.FragulaNavHostFragment_defaultNavHost, false)
            if (defaultNavHost) {
                this.defaultNavHost = true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val navState = navHostController?.saveState()
        if (navState != null) {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, navState)
        }
        if (defaultNavHost) {
            outState.putBoolean(KEY_DEFAULT_NAV_HOST, true)
        }
        if (graphId != 0) {
            outState.putInt(KEY_GRAPH_ID, graphId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewParent?.let {
            if (Navigation.findNavController(it) === navHostController) {
                Navigation.setViewNavController(it, null)
            }
        }
        viewParent = null
    }

    private fun onCreateNavController(navController: NavController) {
        navController.navigatorProvider +=
            DialogFragmentNavigator(requireContext(), childFragmentManager)
        navController.navigatorProvider.addNavigator(createFragmentNavigator())
    }

    private fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return FragmentNavigator(requireContext(), childFragmentManager, containerId)
    }

    companion object {

        const val KEY_GRAPH_ID = "android-fragula-nav:fragment:graphId"
        const val KEY_START_DESTINATION_ARGS = "android-fragula-nav:fragment:startDestinationArgs"

        private const val KEY_NAV_CONTROLLER_STATE = "android-fragula-nav:fragment:navControllerState"
        private const val KEY_DEFAULT_NAV_HOST = "android-fragula-nav:fragment:defaultHost"

        @JvmStatic
        fun findNavController(fragment: Fragment): NavController {
            var findFragment: Fragment? = fragment
            while (findFragment != null) {
                if (findFragment is FragulaNavHostFragment) {
                    return findFragment.navHostController as NavController
                }
                val primaryNavFragment = findFragment.parentFragmentManager
                    .primaryNavigationFragment
                if (primaryNavFragment is FragulaNavHostFragment) {
                    return primaryNavFragment.navHostController as NavController
                }
                findFragment = findFragment.parentFragment
            }

            val view = fragment.view
            if (view != null) {
                return Navigation.findNavController(view)
            }

            val dialogDecorView = (fragment as? DialogFragment)?.dialog?.window?.decorView
            if (dialogDecorView != null) {
                return Navigation.findNavController(dialogDecorView)
            }
            throw IllegalStateException("Fragment $fragment does not have a NavController set")
        }
    }
}