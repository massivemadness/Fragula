package com.fragula2.navigation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.R
import com.fragula2.adapter.NavBackStackAdapter
import com.fragula2.animation.OnSwipeListener
import com.fragula2.animation.SwipeController
import com.fragula2.animation.SwipeDirection
import com.fragula2.animation.SwipeTransformer
import com.fragula2.utils.*
import java.util.concurrent.LinkedBlockingQueue

class SwipeBackFragment : Fragment(R.layout.fragment_swipeback), Navigable, SwipeController {

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> Unit
                ViewPager2.SCROLL_STATE_SETTLING -> {
                    activity?.requestViewLock(true)
                }
                ViewPager2.SCROLL_STATE_IDLE -> {
                    if (scrollToEnd) {
                        val itemCount = navBackStackAdapter?.itemCount ?: 0
                        val currentItem = viewPager?.currentItem ?: 0
                        if (itemCount - 1 > currentItem && !fakeScroll) {
                            userScroll = true
                            navController?.popBackStack()
                            navBackStackAdapter?.pop()
                        }
                    }
                    activity?.requestViewLock(false)
                }
            }
        }
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            scrollToEnd = position + positionOffset < scrollOffset
            scrollOffset = position + positionOffset
            elevation?.isVisible = scrollOffset % 1 > 0
            elevation?.translationX = when (swipeDirection) {
                SwipeDirection.LEFT_TO_RIGHT -> -positionOffsetPixels.toFloat()
                SwipeDirection.RIGHT_TO_LEFT -> positionOffsetPixels.toFloat()
                else -> 0f
            }
            elevation?.translationY = when (swipeDirection) {
                SwipeDirection.TOP_TO_BOTTOM -> -positionOffsetPixels.toFloat()
                SwipeDirection.BOTTOM_TO_TOP -> positionOffsetPixels.toFloat()
                else -> 0f
            }
            onSwipeListeners.forEach { listener ->
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }
    }

    private val onSwipeListeners = mutableListOf<OnSwipeListener>()
    private val delayedTransitions = LinkedBlockingQueue<Runnable>()

    private var viewPager: ViewPager2? = null
    private var elevation: View? = null

    private var navController: NavController? = null
    private var navBackStackAdapter: NavBackStackAdapter? = null
    private var swipeDirection = SwipeDirection.LEFT_TO_RIGHT

    private var userScroll = false
    private var fakeScroll = false

    private var scrollToEnd = false
    private var scrollOffset = 0.0f
    private var scrollDuration = 500L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        swipeDirection = SwipeDirection.find(requireArguments().getInt(ARG_SWIPE_DIRECTION))
        scrollDuration = requireContext()
            .resolveInteger(R.attr.fgl_anim_duration, R.integer.anim_duration_default).toLong()
        elevation = view.findViewById<View>(R.id.elevation).also { elevation ->
            elevation.updateLayoutAngle(swipeDirection,
                requireContext().resolveDimen(R.attr.fgl_elevation, R.dimen.elevation_default)
            )
        }
        viewPager = view.findViewById<ViewPager2>(R.id.viewPager).also { viewPager ->
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager.setPageTransformer(
                SwipeTransformer(swipeDirection,
                    requireContext().resolveFloat(
                        R.attr.fgl_parallax_factor, R.dimen.parallax_factor_default
                    ),
                    requireContext().resolveFloat(
                        R.attr.fgl_dim_amount, R.dimen.dim_amount_default
                    ),
                )
            )
            viewPager.setBackgroundColor(
                requireContext().resolveColor(R.attr.fgl_dim_color, R.color.dim_color_default)
            )
            viewPager.pageOverScrollMode = View.OVER_SCROLL_NEVER
            viewPager.pageSwipeDirection = swipeDirection
            viewPager.adapter = NavBackStackAdapter(this).also { adapter ->
                navBackStackAdapter = adapter
            }
        }
        restoreBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
        navController = null
        navBackStackAdapter = null
        viewPager = null
        elevation = null
    }

    override fun navigate(entry: NavBackStackEntry) {
        if (fakeScroll)
            return delayedTransitions.put { navigate(entry) }
        fakeScroll = true
        activity?.requestViewLock(true)
        navBackStackAdapter?.push(entry)
        viewPager?.fakeDragTo(true, swipeDirection, scrollDuration) {
            activity?.requestViewLock(false)
            fakeScroll = false
            nextTransition()
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry) {
        if (userScroll) {
            userScroll = false
            return
        }
        if (fakeScroll)
            return delayedTransitions.put { popBackStack(popUpTo) }
        fakeScroll = true
        activity?.requestViewLock(true)
        viewPager?.fakeDragTo(false, swipeDirection, scrollDuration) {
            navBackStackAdapter?.pop()
            activity?.requestViewLock(false)
            fakeScroll = false
            nextTransition()
        }
    }

    override fun setUserInputEnabled(enabled: Boolean) {
        viewPager?.isUserInputEnabled = enabled
    }

    override fun isUserInputEnabled(): Boolean {
        return viewPager?.isUserInputEnabled ?: false
    }

    override fun addOnSwipeListener(listener: OnSwipeListener) {
        onSwipeListeners.add(listener)
    }

    override fun removeOnSwipeListener(listener: OnSwipeListener) {
        onSwipeListeners.remove(listener)
    }

    private fun restoreBackStack() {
        viewPager?.currentItem = navController?.backQueue.orEmpty()
            .filter { it.destination is SwipeBackDestination }
            .also { navBackStackAdapter?.addAll(it) }
            .size
    }

    private fun nextTransition() {
        val runnable = delayedTransitions.poll()
        runnable?.run()
    }

    companion object {

        private const val ARG_SWIPE_DIRECTION = "swipe_direction"

        fun newInstance(swipeDirection: SwipeDirection): SwipeBackFragment {
            return SwipeBackFragment().apply {
                arguments = bundleOf(ARG_SWIPE_DIRECTION to swipeDirection.value)
            }
        }
    }
}