package com.fragula2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

internal class SwipeBackFragment : Fragment(R.layout.fragment_swipeback), FragulaInterface {

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE && shouldPop) {
                val itemCount = swipeBackAdapter?.itemCount ?: 0
                val currentItem = viewPager?.currentItem ?: 0
                if (itemCount - 1 > currentItem && !running) {
                    navController?.popBackStack()
                }
            }
        }
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            shouldPop = position + positionOffset < scrollOffset
            scrollOffset = position + positionOffset
        }
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewPager?.requestTransform()
        }
    }

    private val currentItem: Int
        get() = viewPager?.currentItem ?: 0

    private var viewPager: ViewPager2? = null
    private var swipeBackAdapter: SwipeBackAdapter? = null
    private var navController: NavController? = null

    private var running = false
    private var shouldPop = false
    private var scrollOffset = 0.0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        viewPager = view.findViewById<ViewPager2?>(R.id.viewPager).also { viewPager ->
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager.setPageTransformer(SwipeBackTransformer())
            viewPager.pageOverScrollMode = View.OVER_SCROLL_NEVER
            viewPager.adapter = SwipeBackAdapter(this).also { adapter ->
                swipeBackAdapter = adapter
            }
        }
        restoreBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
        swipeBackAdapter = null
        navController = null
        viewPager = null
    }

    override fun navigate(entry: FragulaEntry) {
        swipeBackAdapter?.push(entry)
        viewPager?.isUserInputEnabled = false
        viewPager?.setCurrentItemInternal(currentItem + 1) {
            viewPager?.isUserInputEnabled = true
        }
    }

    override fun popBackStack() {
        if (!running) {
            running = true
            viewPager?.isUserInputEnabled = false
            viewPager?.setCurrentItemInternal(currentItem - 1) {
                swipeBackAdapter?.pop()
                viewPager?.isUserInputEnabled = true
                running = false
            }
        }
    }

    private fun restoreBackStack() {
        viewPager?.currentItem = navController?.backQueue.orEmpty()
            .filterNot { it.destination is NavGraph }
            .map(NavBackStackEntry::toFragulaEntry)
            .also { swipeBackAdapter?.addAll(it) }
            .size
    }
}