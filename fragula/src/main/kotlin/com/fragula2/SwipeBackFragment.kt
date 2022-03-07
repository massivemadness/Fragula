package com.fragula2

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blacksquircle.fragula.R

class SwipeBackFragment : Fragment(R.layout.fragment_swipeback) {

    private val initialClassName by lazy { arguments?.getString(ARG_CLASSNAME) }
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            // popBackStack()
        }
    }

    private var viewPager: ViewPager2? = null
    private var swipeBackAdapter: SwipeBackAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById<ViewPager2?>(R.id.viewPager).also { viewPager ->
            viewPager.setPageTransformer(SwipeBackPageTransformer())
            viewPager.adapter = SwipeBackAdapter(this).also {
                swipeBackAdapter = it
            }
        }

        if (initialClassName != null) {
            navigate(initialClassName ?: throw IllegalArgumentException("Invalid fragment"))
            arguments?.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        swipeBackAdapter = null
        viewPager = null
    }

    fun contains(className: String): Boolean {
        return swipeBackAdapter?.contains(className) ?: false
    }

    fun navigate(className: String) {
        swipeBackAdapter?.push(className)
        viewPager?.currentItem = viewPager?.currentItem?.plus(1) ?: -1
    }

    fun popBackStack() {
        swipeBackAdapter?.pop()
        // viewPager?.currentItem = viewPager?.currentItem?.minus(1) ?: -1
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