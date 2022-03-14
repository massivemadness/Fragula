package com.fragula2.navigation

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fragula2.R
import com.fragula2.adapter.FragulaEntry
import com.fragula2.adapter.SwipeBackAdapter
import com.fragula2.adapter.SwipeBackTransformer
import com.fragula2.animation.OnSwipeListener
import com.fragula2.animation.SwipeController
import com.fragula2.utils.*
import com.fragula2.utils.fakeDragTo
import com.fragula2.utils.pageOverScrollMode
import com.fragula2.utils.resolveColor
import com.fragula2.utils.toFragulaEntry
import java.util.concurrent.LinkedBlockingQueue

class SwipeBackFragment : Fragment(R.layout.fragment_swipeback), Navigable, SwipeController {

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> Unit
                ViewPager2.SCROLL_STATE_SETTLING -> {
                    requestViewLock(true)
                }
                ViewPager2.SCROLL_STATE_IDLE -> {
                    if (scrollToEnd) {
                        val itemCount = swipeBackAdapter?.itemCount ?: 0
                        val currentItem = viewPager?.currentItem ?: 0
                        if (itemCount - 1 > currentItem && !fakeScroll) {
                            navController?.popBackStack()
                        }
                    }
                    requestViewLock(false)
                }
            }
        }
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            scrollToEnd = position + positionOffset < scrollOffset
            scrollOffset = position + positionOffset
            elevation?.translationX = -positionOffsetPixels.toFloat()
            elevation?.isVisible = scrollOffset % 1 > 0
            onSwipeListeners.forEach { listener ->
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }
    }

    private val onSwipeListeners = mutableListOf<OnSwipeListener>()
    private val delayedTransitions = LinkedBlockingQueue<Runnable>()
    private val currentItem: Int
        get() = viewPager?.currentItem ?: 0

    private var viewPager: ViewPager2? = null
    private var elevation: View? = null

    private var swipeBackAdapter: SwipeBackAdapter? = null
    private var navController: NavController? = null

    private var fakeScroll = false
    private var scrollToEnd = false
    private var scrollOffset = 0.0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        elevation = view.findViewById(R.id.elevation)
        viewPager = view.findViewById<ViewPager2>(R.id.viewPager).also { viewPager ->
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager.setPageTransformer(SwipeBackTransformer(
                requireContext().resolveFloat(R.attr.fgl_dim_amount, R.dimen.dim_amount_default)
            ))
            viewPager.setBackgroundColor(
                requireContext().resolveColor(R.attr.fgl_dim_color, R.color.dim_color_default)
            )
            viewPager.pageOverScrollMode = View.OVER_SCROLL_NEVER
            viewPager.adapter = SwipeBackAdapter(this).also { adapter ->
                swipeBackAdapter = adapter
            }
        }
        restoreBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
        swipeBackAdapter = null
        navController = null
        viewPager = null
        elevation = null
    }

    override fun navigate(entry: FragulaEntry) {
        if (fakeScroll)
            return delayedTransitions.put { navigate(entry) }
        fakeScroll = true
        requestViewLock(true)
        swipeBackAdapter?.push(entry)
        viewPager?.fakeDragTo(currentItem + 1) {
            requestViewLock(false)
            fakeScroll = false
            nextTransition()
        }
    }

    override fun popBackStack() {
        if (fakeScroll)
            return delayedTransitions.put(::popBackStack)
        fakeScroll = true
        requestViewLock(true)
        viewPager?.fakeDragTo(currentItem - 1) {
            swipeBackAdapter?.pop()
            requestViewLock(false)
            fakeScroll = false
            nextTransition()
        }
    }

    override fun addOnSwipeListener(listener: OnSwipeListener) {
        onSwipeListeners.add(listener)
    }

    override fun removeOnSwipeListener(listener: OnSwipeListener) {
        onSwipeListeners.remove(listener)
    }

    private fun restoreBackStack() {
        viewPager?.currentItem = navController?.backQueue.orEmpty()
            .filter { it.destination is SwipeBackNavigator.Destination }
            .map(NavBackStackEntry::toFragulaEntry)
            .also { swipeBackAdapter?.addAll(it) }
            .size
    }

    private fun requestViewLock(locked: Boolean) {
        if (locked) {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    private fun nextTransition() {
        val runnable = delayedTransitions.poll()
        runnable?.run()
    }
}