package com.fragulo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fragulo.adapter.NavigatorAdapter
import com.fragulo.common.Arg
import com.fragulo.common.FragmentNavigator
import com.fragulo.common.SwipeDirection
import com.fragulo.listener.OnFragmentNavigatorListener
import java.io.Serializable

class Navigator : FragmentNavigator {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private lateinit var fragmentManager: FragmentManager
    private var navigatorAdapter: NavigatorAdapter? = null
    private var onPageChangeListener: OnPageChangeListener? = null

    var currentFragment: Fragment? = null
        private set
    var previousFragment: Fragment? = null
        private set
    var isBlockTouchEvent: Boolean = false
        private set

    var onPageScrollStateChanged: ((state: Int) -> Unit)? = null
    var onPageSelected: ((position: Int) -> Unit)? = null
    var onNotifyDataChanged: ((fragmentCount: Int) -> Unit)? = null
    var onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null

    fun init(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
        initAdapter()
        offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        setBackgroundColor(Color.BLACK)
    }

    fun addFragment(
        fragment: Fragment,
        vararg args: Arg<*, *>) {
        fragment.arguments = getBundle(*args)
        addFragment(fragment)
    }

    fun addFragment(fragment: Fragment) {
        if (fragment.isAdded || navigatorAdapter == null)
            return

        if (navigatorAdapter!!.getFragmentsCount() > 0) {
            isBlockTouchEvent = true
        }
        navigatorAdapter!!.addFragment(fragment)
        Handler().postDelayed({
            goToNextFragment()
        }, 30)
    }

    fun replaceCurrentFragment(newFragment: Fragment, vararg args: Arg<*, *>) {
        newFragment.arguments = getBundle(*args)
        replaceCurrentFragment(newFragment)
    }

    fun replaceCurrentFragment(newFragment: Fragment) {
        navigatorAdapter?.replaceFragment((navigatorAdapter?.count ?: 1) - 1, newFragment)
    }

    fun replaceFragmentByPosition(newFragment: Fragment, position: Int, vararg args: Arg<*, *>) {
        newFragment.arguments = getBundle(*args)
        navigatorAdapter?.replaceFragment(position, newFragment)
    }

    fun replaceFragmentByPosition(newFragment: Fragment, position: Int) {
        navigatorAdapter?.replaceFragment(position, newFragment)
    }

    private fun initAdapter() {
        navigatorAdapter = NavigatorAdapter(fragmentManager)
        setNavigatorChangeListener()
        adapter = navigatorAdapter
    }

    fun fragments() : ArrayList<Fragment>? {
        return navigatorAdapter?.fragments
    }

    fun fragmentsCount(): Int = navigatorAdapter?.count ?: 0

    private fun setNavigatorChangeListener() {
        onPageChangeListener = object : OnPageChangeListener {
            var sumPositionAndPositionOffset = 0f
            var swipeDirection: SwipeDirection = SwipeDirection.NONE
            override fun onPageSelected(position: Int) {
                onPageSelected?.invoke(position)
            }
            override fun onNotifyDataChanged(itemCount: Int) {
                setCurrentFragment()
                setPreviousFragment()
                onNotifyDataChanged?.invoke(itemCount)
            }
            override fun onPageScrollStateChanged(state: Int) {
                isBlockTouchEvent = state == SCROLL_STATE_SETTLING
                when (state) {
                    SCROLL_STATE_IDLE -> {
                        if (navigatorAdapter == null) return
                        if (swipeDirection == SwipeDirection.LEFT) {
                            while ((navigatorAdapter!!.getSizeListOfFragments() - 1) > currentItem) {
                                navigatorAdapter?.removeLastFragment()
                            }
                            if (currentFragment != null && currentFragment is OnFragmentNavigatorListener) {
                                (currentFragment as OnFragmentNavigatorListener).onReturnedFragment()
                            }
                        } else {
                            if (currentFragment != null && currentFragment is OnFragmentNavigatorListener) {
                                (currentFragment as OnFragmentNavigatorListener).onOpenedFragment()
                            }
                        }
                    }
                }
                onPageScrollStateChanged?.invoke(state)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                swipeDirection =
                    if (position + positionOffset < sumPositionAndPositionOffset) SwipeDirection.LEFT
                    else SwipeDirection.RIGHT
                sumPositionAndPositionOffset = position + positionOffset
                onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
            }
        }
        addOnPageChangeListener(onPageChangeListener!!)
    }

    private fun setCurrentFragment() {
        if (navigatorAdapter == null) return
        if (navigatorAdapter!!.fragments.isNotEmpty()) {
            currentFragment = navigatorAdapter!!.fragments.last()
        }
    }

    private fun setPreviousFragment() {
        if (navigatorAdapter == null) return
        previousFragment = if (navigatorAdapter!!.getFragmentsCount() > 1) {
            navigatorAdapter!!.fragments[navigatorAdapter!!.getFragmentsCount() - 2]
        } else null
    }

    fun release() {
        onPageChangeListener?.let {
            removeOnPageChangeListener(it)
        }
        onPageChangeListener = null
        onPageScrollStateChanged = null
        onPageSelected = null
        onNotifyDataChanged = null
        onPageScrolled = null
        currentFragment = null
        previousFragment = null
    }

    private fun getBundle(vararg args: Arg<*, *>): Bundle {
        val bundle = Bundle()
        for (arg in args) {
            val key = arg.key as String?
            val value = arg.value

            if (value is Boolean) {
                bundle.putBoolean(key, (value as Boolean?)!!)
            }
            if (value is Byte) {
                bundle.putByte(key, (value as Byte?)!!)
            }
            if (value is Char) {
                bundle.putChar(key, (value as Char?)!!)
            }
            if (value is Int) {
                bundle.putInt(key, (value as Int?)!!)
            }
            if (value is Long) {
                bundle.putLong(key, (value as Long?)!!)
            }
            if (value is Float) {
                bundle.putFloat(key, (value as Float?)!!)
            }
            if (value is Double) {
                bundle.putDouble(key, (value as Double?)!!)
            }
            if (value is String) {
                bundle.putString(key, value as String?)
            }
            if (value is Parcelable) {
                bundle.putParcelable(key, value as Parcelable?)
            }
            if (value is Serializable) {
                bundle.putSerializable(key, value as Serializable?)
            }
        }
        return bundle
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

    companion object {
        private const val OFFSCREEN_PAGE_LIMIT = 100
    }
}