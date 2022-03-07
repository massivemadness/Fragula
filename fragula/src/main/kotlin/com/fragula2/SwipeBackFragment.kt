package com.fragula2

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class SwipeBackFragment : Fragment(R.layout.fragment_swipeback) {

    private val initialClassName by lazy { arguments?.getString(ARG_CLASSNAME) }
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE && shouldPop) {
                val itemCount = swipeBackAdapter?.itemCount ?: 0
                val currentItem = viewPager?.currentItem ?: 0
                if (itemCount - 1 > currentItem) {
                    internalPopBackStack()
                }
            }
        }
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            shouldPop = position + positionOffset < scrollOffset
            scrollOffset = position + positionOffset
        }
    }

    private var viewPager: ViewPager2? = null
    private var swipeBackAdapter: SwipeBackAdapter? = null

    private var shouldPop = false
    private var scrollOffset = 0.0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById<ViewPager2?>(R.id.viewPager).also { viewPager ->
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager.setPageTransformer(SwipeBackTransformer())
            viewPager.adapter = SwipeBackAdapter(this).also { adapter ->
                swipeBackAdapter = adapter
            }
        }

        if (initialClassName != null) {
            navigate(initialClassName ?: throw IllegalArgumentException("Invalid fragment"))
            arguments?.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.unregisterOnPageChangeCallback(onPageChangeCallback)
        swipeBackAdapter = null
        viewPager = null
    }

    fun navigate(className: String) {
        swipeBackAdapter?.push(className)
        viewPager?.also { viewPager ->
            viewPager.currentItem = viewPager.currentItem + 1
        }
    }

    fun popBackStack() {
        swipeBackAdapter?.pop()
    }

    private fun internalPopBackStack() {
        (parentFragment as? FragulaNavHostFragment)?.run {
            navController.popBackStack()
        }
    }

    companion object {

        private const val ARG_CLASSNAME = "className"

        fun newInstance(className: String): SwipeBackFragment {
            return SwipeBackFragment().apply {
                arguments = bundleOf(ARG_CLASSNAME to className)
            }
        }
    }
}