package com.fragulo.common

import android.R
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.EdgeEffect
import android.widget.Scroller
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.view.AbsSavedState
import com.fragulo.adapter.base.PagerAdapter
import com.fragulo.extensions.invisible
import com.fragulo.extensions.tryCatch
import com.fragulo.extensions.visible
import com.fragulo.transformer.NavigatorPageTransformer
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Layout manager that allows the user to flip left and right
 * through pages of data.  You supply an implementation of a
 * [PagerAdapter] to generate the pages that the view shows.
 *
 *
 * ViewPager is most often used in conjunction with [android.app.Fragment],
 * which is a convenient way to supply and manage the lifecycle of each page.
 * There are standard adapters implemented for using fragments with the ViewPager,
 * which cover the most common use cases.  These are
 * [FragmentStatePagerAdapter]; each of these
 * classes have simple code showing how to build a full user interface
 * with them.
 *
 *
 * Views which are annotated with the [FragmentNavigator.DecorView] annotation are treated as
 * part of the view pagers 'decor'. Each decor view's position can be controlled via
 * its `android:layout_gravity` attribute. For example:
 *
 * <pre>
 * &lt;ViewPager
 * android:layout_width=&quot;match_parent&quot;
 * android:layout_height=&quot;match_parent&quot;&gt;
 *
 * &lt;PagerTitleStrip
 * android:layout_width=&quot;match_parent&quot;
 * android:layout_height=&quot;wrap_content&quot;
 * android:layout_gravity=&quot;top&quot; /&gt;
 *
 * &lt;/ViewPager&gt;
</pre> *
 *
 *
 * For more information about how to use ViewPager, read [Creating Swipe Views with
 * Tabs]({@docRoot}training/implementing-navigation/lateral.html).
 *
 *
 * You can find examples of using ViewPager in the API 4+ Support Demos and API 13+ Support Demos
 * sample code.
 */
open class FragmentNavigator : ViewGroup {
    /**
     * Used to track what the expected number of items in the adapter should be.
     * If the app changes this when we don't expect it, we'll throw a big obnoxious exception.
     */
    private var mExpectedAdapterCount = 0

    class ItemInfo {
        var `object`: Any? = null
        var position = 0
        var scrolling = false
        var widthFactor = 0f
        var offset = 0f
    }

    private val mItems = ArrayList<ItemInfo>()
    private val mTempItem = ItemInfo()
    private val mTempRect = Rect()
    var mAdapter: PagerAdapter? = null
    var mCurItem = 0 // Index of currently displayed page. = 0
    private var mRestoredCurItem = -1
    private var mRestoredAdapterState: Parcelable? = null
    private var mRestoredClassLoader: ClassLoader? = null
    private var mScroller: Scroller? = null
    private var mIsScrollStarted = false
    private var mObserver: PagerObserver? = null
    private var mPageMargin = 0
    private var mMarginDrawable: Drawable? = null
    private var mTopPageBounds = 0
    private var mBottomPageBounds = 0
    // Offsets of the first and last items, if known.
    // Set during population, used to determine if we are at the beginning
    // or end of the pager data set during touch scrolling.
    private var mFirstOffset = -Float.MAX_VALUE
    private var mLastOffset = Float.MAX_VALUE
    private var mChildWidthMeasureSpec = 0
    private var mChildHeightMeasureSpec = 0
    private var mInLayout = false
    private var mScrollingCacheEnabled = false
    private var mPopulatePending = false
    private var mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES
    private var mIsBeingDragged = false
    private var mIsUnableToDrag = false
    private var mDefaultGutterSize = 0
    private var mGutterSize = 0
    private var mTouchSlop = 0
    /**
     * Position of the last motion event.
     */
    private var mLastMotionX = 0f
    private var mLastMotionY = 0f
    private var mInitialMotionX = 0f
    private var mInitialMotionY = 0f
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private var mActivePointerId = INVALID_POINTER
    /**
     * Determines speed during touch scrolling
     */
    private var mVelocityTracker: VelocityTracker? = null
    private var mMinimumVelocity = 0
    private var mMaximumVelocity = 0
    private var mFlingDistance = 0
    private var mCloseEnough = 0
    /**
     * Returns true if a fake drag is in progress.
     *
     * @return true if currently in a fake drag, false otherwise.
     *
     * @see .beginFakeDrag
     * @see .fakeDragBy
     * @see .endFakeDrag
     */
    var isFakeDragging = false
        private set
    private var mFakeDragBeginTime: Long = 0
    private var mLeftEdge: EdgeEffect? = null
    private var mRightEdge: EdgeEffect? = null
    private var mFirstLayout = true
    private var mNeedCalculatePageOffsets = false
    private var mCalledSuper = false
    private var mDecorChildCount = 0
    private var mOnPageChangeListeners: MutableList<OnPageChangeListener>? = null
    private var mOnPageChangeListener: OnPageChangeListener? = null
    private var mInternalPageChangeListener: OnPageChangeListener? = null
    private var mAdapterChangeListeners: MutableList<OnAdapterChangeListener>? = null
    private var mPageTransformer: PageTransformer? = null
    private var mPageTransformerLayerType = 0
    private var mDrawingOrder = 0
    private var mDrawingOrderedChildren: ArrayList<View>? = null
    private val mEndScrollRunnable = Runnable {
        setScrollState(SCROLL_STATE_IDLE)
        populate()
    }
    private var mScrollState = SCROLL_STATE_IDLE

    private var initialXValue: Float = 0f
    private var direction: SwipeDirection = SwipeDirection.ALL
    var isChangingChildVisibility: Boolean = true
    private var durationFactor = 1f

    enum class SwipeDirection {
        ALL, LEFT, RIGHT, NONE
    }

    /**
     * Callback interface for responding to changing state of the selected page.
     */
    interface OnPageChangeListener {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position Position index of the first page currently being displayed.
         * Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int)

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        fun onPageSelected(position: Int)

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see FragmentNavigator.SCROLL_STATE_IDLE
         *
         * @see FragmentNavigator.SCROLL_STATE_DRAGGING
         *
         * @see FragmentNavigator.SCROLL_STATE_SETTLING
         */
        fun onPageScrollStateChanged(state: Int)

        fun onNotifyDataChanged(itemCount: Int)
    }

    /**
     * A PageTransformer is invoked whenever a visible/attached page is scrolled.
     * This offers an opportunity for the application to apply a custom transformation
     * to the page views using animation properties.
     *
     *
     * As property animation is only supported as of Android 3.0 and forward,
     * setting a PageTransformer on a ViewPager on earlier platform versions will
     * be ignored.
     */
    interface PageTransformer {
        /**
         * Apply a property transformation to the given page.
         *
         * @param page Apply the transformation to this page
         * @param position Position of page relative to the current front-and-center
         * position of the pager. 0 is front and center. 1 is one full
         * page position to the right, and -1 is one page position to the left.
         */
        fun transformPage(page: View, position: Float)
    }

    /**
     * Callback interface for responding to adapter changes.
     */
    interface OnAdapterChangeListener {
        /**
         * Called when the adapter for the given view pager has changed.
         *
         * @param fragmentNavigator  ViewPager where the adapter change has happened
         * @param oldAdapter the previously set adapter
         * @param newAdapter the newly set adapter
         */
        fun onAdapterChanged(fragmentNavigator: FragmentNavigator,
                             oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?)
    }

    /**
     * Annotation which allows marking of views to be decoration views when added to a view
     * pager.
     *
     *
     * Views marked with this annotation can be added to the view pager with a layout resource.
     * An example being.
     *
     *
     * You can also control whether a view is a decor view but setting
     * [FragmentNavigator.LayoutParams.isDecor] on the child's layout params.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
    @Inherited
    annotation class DecorView {}

    constructor(context: Context) : super(context) {
        initViewPager()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViewPager()
    }

    private fun initViewPager() {
        setWillNotDraw(false)
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
        isFocusable = true
        val context = context
        mScroller = Scroller(context, sInterpolator)
        val configuration = ViewConfiguration.get(context)
        val density = context.resources.displayMetrics.density
        mTouchSlop = configuration.scaledPagingTouchSlop
        mMinimumVelocity = (MIN_FLING_VELOCITY * density).toInt()
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        mLeftEdge = EdgeEffect(context)
        mRightEdge = EdgeEffect(context)
        mFlingDistance = (MIN_DISTANCE_FOR_FLING * density).toInt()
        mCloseEnough = (CLOSE_ENOUGH * density).toInt()
        mDefaultGutterSize = (DEFAULT_GUTTER_SIZE * density).toInt()
        ViewCompat.setAccessibilityDelegate(this, MyAccessibilityDelegate())
        if (ViewCompat.getImportantForAccessibility(this)
                == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            ViewCompat.setImportantForAccessibility(this,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        }
        ViewCompat.setOnApplyWindowInsetsListener(this,
                object : androidx.core.view.OnApplyWindowInsetsListener {
                    private val mTempRect = Rect()
                    override fun onApplyWindowInsets(v: View,
                                                     originalInsets: WindowInsetsCompat): WindowInsetsCompat { // First let the ViewPager itself try and consume them...
                        val applied = ViewCompat.onApplyWindowInsets(v, originalInsets)
                        if (applied.isConsumed) { // If the ViewPager consumed all insets, return now
                            return applied
                        }
                        // Now we'll manually dispatch the insets to our children. Since ViewPager
                        // children are always full-height, we do not want to use the standard
                        // ViewGroup dispatchApplyWindowInsets since if child 0 consumes them,
                        // the rest of the children will not receive any insets. To workaround this
                        // we manually dispatch the applied insets, not allowing children to
                        // consume them from each other. We do however keep track of any insets
                        // which are consumed, returning the union of our children's consumption
                        val res = mTempRect
                        res.left = applied.systemWindowInsetLeft
                        res.top = applied.systemWindowInsetTop
                        res.right = applied.systemWindowInsetRight
                        res.bottom = applied.systemWindowInsetBottom
                        var i = 0
                        val count = childCount
                        while (i < count) {
                            val childInsets = ViewCompat
                                    .dispatchApplyWindowInsets(getChildAt(i), applied)
                            // Now keep track of any consumed by tracking each dimension's min
                            // value
                            res.left = Math.min(childInsets.systemWindowInsetLeft,
                                    res.left)
                            res.top = Math.min(childInsets.systemWindowInsetTop,
                                    res.top)
                            res.right = Math.min(childInsets.systemWindowInsetRight,
                                    res.right)
                            res.bottom = Math.min(childInsets.systemWindowInsetBottom,
                                    res.bottom)
                            i++
                        }
                        // Now return a new WindowInsets, using the consumed window insets
                        return applied.replaceSystemWindowInsets(
                                res.left, res.top, res.right, res.bottom)
                    }
                })
        setPageTransformer(false, NavigatorPageTransformer())
        setAllowedSwipeDirection(SwipeDirection.LEFT)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(mEndScrollRunnable)
        // To be on the safe side, abort the scroller
        if (mScroller != null && !mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
        super.onDetachedFromWindow()
    }

    fun setScrollState(newState: Int) {
        if (mScrollState == newState) {
            return
        }
        mScrollState = newState
        if (mPageTransformer != null) { // PageTransformers can do complex things that benefit from hardware layers.
            enableLayers(newState != SCROLL_STATE_IDLE)
        }
        dispatchOnScrollStateChanged(newState)
    }

    private fun removeNonDecorViews() {
        var i = 0
        while (i < childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            if (!lp.isDecor) {
                removeViewAt(i)
                i--
            }
            i++
        }
    }

    /**
     * Retrieve the current adapter supplying pages.
     *
     * @return The currently registered PagerAdapter
     */// Dispatch the change to any listeners
    /**
     * Set a PagerAdapter that will supply views for this pager as needed.
     *
     * @param adapter Adapter to use
     */
    var adapter: PagerAdapter?
        get() = mAdapter
        set(adapter) {
            if (mAdapter != null) {
                mAdapter!!.setViewPagerObserver(null)
                mAdapter!!.startUpdate(this)
                for (i in mItems.indices) {
                    val ii = mItems[i]
                    mAdapter!!.destroyItem(this, ii.position, ii.`object`!!)
                }
                mAdapter!!.finishUpdate(this)
                mItems.clear()
                removeNonDecorViews()
                mCurItem = 0
                scrollTo(0, 0)
            }
            val oldAdapter = mAdapter
            mAdapter = adapter
            mExpectedAdapterCount = 0
            if (mAdapter != null) {
                if (mObserver == null) {
                    mObserver = PagerObserver()
                }
                mAdapter!!.setViewPagerObserver(mObserver)
                mPopulatePending = false
                val wasFirstLayout = mFirstLayout
                mFirstLayout = true
                mExpectedAdapterCount = mAdapter!!.count
                if (mRestoredCurItem >= 0) {
                    mAdapter!!.restoreState(mRestoredAdapterState, mRestoredClassLoader)
                    setCurrentItemInternal(mRestoredCurItem, false, true)
                    mRestoredCurItem = -1
                    mRestoredAdapterState = null
                    mRestoredClassLoader = null
                } else if (!wasFirstLayout) {
                    populate()
                } else {
                    requestLayout()
                }
            }
            // Dispatch the change to any listeners
            if (mAdapterChangeListeners != null && !mAdapterChangeListeners!!.isEmpty()) {
                var i = 0
                val count = mAdapterChangeListeners!!.size
                while (i < count) {
                    mAdapterChangeListeners!![i].onAdapterChanged(this, oldAdapter, adapter)
                    i++
                }
            }
        }

    /**
     * Add a listener that will be invoked whenever the adapter for this ViewPager changes.
     *
     * @param listener listener to add
     */
    fun addOnAdapterChangeListener(listener: OnAdapterChangeListener) {
        if (mAdapterChangeListeners == null) {
            mAdapterChangeListeners = ArrayList()
        }
        mAdapterChangeListeners!!.add(listener)
    }

    /**
     * Remove a listener that was previously added via
     * [.addOnAdapterChangeListener].
     *
     * @param listener listener to remove
     */
    fun removeOnAdapterChangeListener(listener: OnAdapterChangeListener) {
        if (mAdapterChangeListeners != null) {
            mAdapterChangeListeners!!.remove(listener)
        }
    }

    private val clientWidth: Int
        private get() = measuredWidth - paddingLeft - paddingRight

    /**
     * Set the currently selected page.
     *
     * @param item Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    fun goToFragment(item: Int, smoothScroll: Boolean) {
        mPopulatePending = false
        setCurrentItemInternal(item, smoothScroll, false)
    }

    fun goToPreviousFragmentAndRemoveLast() {
        goToFragment(currentItem - 1, true)
    }

    fun goToNextFragment() {
        goToFragment(currentItem + 1, true)
    }

    /**
     * Set the currently selected page. If the ViewPager has already been through its first
     * layout with its current adapter there will be a smooth animated transition between
     * the current item and the specified item.
     *
     * @param item Item index to select
     */
    var currentItem: Int
        get() = mCurItem
        set(item) {
            mPopulatePending = false
            setCurrentItemInternal(item, !mFirstLayout, false)
        }

    fun setCurrentItemInternal(item: Int, smoothScroll: Boolean, always: Boolean) {
        setCurrentItemInternal(item, smoothScroll, always, 0)
    }

    fun setCurrentItemInternal(item: Int, smoothScroll: Boolean, always: Boolean, velocity: Int) {
        var item = item
        if (mAdapter == null || mAdapter!!.count <= 0) {
            setScrollingCacheEnabled(false)
            return
        }
        if (!always && mCurItem == item && mItems.size != 0) {
            setScrollingCacheEnabled(false)
            return
        }
        if (item < 0) {
            item = 0
        } else if (item >= mAdapter!!.count) {
            item = mAdapter!!.count - 1
        }
        val pageLimit = mOffscreenPageLimit
        if (item > mCurItem + pageLimit || item < mCurItem - pageLimit) { // We are doing a jump by more than one page.  To avoid
            // glitches, we want to keep all current pages in the view
            // until the scroll ends.
            for (i in mItems.indices) {
                mItems[i].scrolling = true
            }
        }
        val dispatchSelected = mCurItem != item
        if (mFirstLayout) { // We don't have any idea how big we are yet and shouldn't have any pages either.
            // Just set things up and let the pending layout handle things.
            mCurItem = item
            if (dispatchSelected) {
                dispatchOnPageSelected(item)
            }
            requestLayout()
        } else {
            populate(item)
            scrollToItem(item, smoothScroll, velocity, dispatchSelected)
        }
    }

    private fun scrollToItem(item: Int, smoothScroll: Boolean, velocity: Int,
                             dispatchSelected: Boolean) {
        val curInfo = infoForPosition(item)
        var destX = 0
        if (curInfo != null) {
            val width = clientWidth
            destX = (width * Math.max(mFirstOffset,
                    Math.min(curInfo.offset, mLastOffset))).toInt()
        }
        if (smoothScroll) {
            smoothScrollTo(destX, 0, velocity)
            if (dispatchSelected) {
                dispatchOnPageSelected(item)
            }
        } else {
            if (dispatchSelected) {
                dispatchOnPageSelected(item)
            }
            completeScroll(false)
            scrollTo(destX, 0)
            pageScrolled(destX)
        }
    }


    /**
     * Add a listener that will be invoked whenever the page changes or is incrementally
     * scrolled. See [FragmentNavigator.OnPageChangeListener].
     *
     *
     * Components that add a listener should take care to remove it when finished.
     * Other components that take ownership of a view may call [.clearOnPageChangeListeners]
     * to remove all attached listeners.
     *
     * @param listener listener to add
     */
    protected fun addOnPageChangeListener(listener: OnPageChangeListener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = ArrayList()
        }
        mOnPageChangeListeners!!.add(listener)
    }

    /**
     * Remove a listener that was previously added via
     * [.addOnPageChangeListener].
     *
     * @param listener listener to remove
     */
    fun removeOnPageChangeListener(listener: OnPageChangeListener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners!!.remove(listener)
        }
    }

    /**
     * Remove all listeners that are notified of any changes in scroll state or position.
     */
    fun clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners!!.clear()
        }
    }

    /**
     * Sets a [FragmentNavigator.PageTransformer] that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding behavior.
     *
     *
     * *Note:* By default, calling this method will cause contained pages to use
     * [View.LAYER_TYPE_HARDWARE]. This layer type allows custom alpha transformations,
     * but it will cause issues if any of your pages contain a [android.view.SurfaceView]
     * and you have not called [android.view.SurfaceView.setZOrderOnTop] to put that
     * [android.view.SurfaceView] above your app content. To disable this behavior, call
     * [.setPageTransformer] and pass
     * [View.LAYER_TYPE_NONE] for `pageLayerType`.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     * to be drawn from last to first instead of first to last.
     * @param transformer PageTransformer that will modify each page's animation properties
     */
    fun setPageTransformer(reverseDrawingOrder: Boolean,
                           transformer: PageTransformer?) {
        setPageTransformer(reverseDrawingOrder, transformer, View.LAYER_TYPE_HARDWARE)
    }

    /**
     * Sets a [FragmentNavigator.PageTransformer] that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding behavior.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     * to be drawn from last to first instead of first to last.
     * @param transformer PageTransformer that will modify each page's animation properties
     * @param pageLayerType View layer type that should be used for ViewPager pages. It should be
     * either [View.LAYER_TYPE_HARDWARE],
     * [View.LAYER_TYPE_SOFTWARE], or
     * [View.LAYER_TYPE_NONE].
     */
    fun setPageTransformer(reverseDrawingOrder: Boolean,
                           transformer: PageTransformer?, pageLayerType: Int) {
        val hasTransformer = transformer != null
        val needsPopulate = hasTransformer != (mPageTransformer != null)
        mPageTransformer = transformer
        isChildrenDrawingOrderEnabled = hasTransformer
        if (hasTransformer) {
            mDrawingOrder = if (reverseDrawingOrder) DRAW_ORDER_REVERSE else DRAW_ORDER_FORWARD
            mPageTransformerLayerType = pageLayerType
        } else {
            mDrawingOrder = DRAW_ORDER_DEFAULT
        }
        if (needsPopulate) populate()
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val index = if (mDrawingOrder == DRAW_ORDER_REVERSE) childCount - 1 - i else i
        return (mDrawingOrderedChildren!![index].layoutParams as LayoutParams).childIndex
    }

    /**
     * Set a separate OnPageChangeListener for internal use by the support library.
     *
     * @param listener Listener to set
     * @return The old listener that was set, if any.
     */
    fun setInternalPageChangeListener(listener: OnPageChangeListener?): OnPageChangeListener? {
        val oldListener = mInternalPageChangeListener
        mInternalPageChangeListener = listener
        return oldListener
    }

    /**
     * Returns the number of pages that will be retained to either side of the
     * current page in the view hierarchy in an idle state. Defaults to 1.
     *
     * @return How many pages will be kept offscreen on either side
     * @see .setOffscreenPageLimit
     */
    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     *
     * This is offered as an optimization. If you know in advance the number
     * of pages you will need to support or have lazy-loading mechanisms in place
     * on your pages, tweaking this setting can have benefits in perceived smoothness
     * of paging animations and interaction. If you have a small number of pages (3-4)
     * that you can keep active all at once, less time will be spent in layout for
     * newly created view subtrees as the user pages back and forth.
     *
     *
     * You should keep this limit low, especially if your pages have complex layouts.
     * This setting defaults to 1.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     */
    var offscreenPageLimit: Int
        get() = mOffscreenPageLimit
        set(limit) {
            var limit = limit
            if (limit < DEFAULT_OFFSCREEN_PAGES) {
                Log.w(TAG, "Requested offscreen page limit " + limit + " too small; defaulting to "
                        + DEFAULT_OFFSCREEN_PAGES)
                limit = DEFAULT_OFFSCREEN_PAGES
            }
            if (limit != mOffscreenPageLimit) {
                mOffscreenPageLimit = limit
                populate()
            }
        }

    /**
     * Return the margin between pages.
     *
     * @return The size of the margin in pixels
     */
    /**
     * Set the margin between pages.
     *
     * @param marginPixels Distance between adjacent pages in pixels
     * @see .getPageMargin
     * @see .setPageMarginDrawable
     * @see .setPageMarginDrawable
     */
    var pageMargin: Int
        get() = mPageMargin
        set(marginPixels) {
            val oldMargin = mPageMargin
            mPageMargin = marginPixels
            val width = width
            recomputeScrollPosition(width, width, marginPixels, oldMargin)
            requestLayout()
        }

    /**
     * Set a drawable that will be used to fill the margin between pages.
     *
     * @param d Drawable to display between pages
     */
    fun setPageMarginDrawable(d: Drawable?) {
        mMarginDrawable = d
        if (d != null) refreshDrawableState()
        setWillNotDraw(d == null)
        invalidate()
    }

    /**
     * Set a drawable that will be used to fill the margin between pages.
     *
     * @param resId Resource ID of a drawable to display between pages
     */
    fun setPageMarginDrawable(@DrawableRes resId: Int) {
        setPageMarginDrawable(ContextCompat.getDrawable(context, resId))
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mMarginDrawable
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val d = mMarginDrawable
        if (d != null && d.isStateful) {
            d.state = drawableState
        }
    }

    // We want the duration of the page snap animation to be influenced by the distance that
// the screen has to travel, however, we don't want this duration to be effected in a
// purely linear fashion. Instead, we use this method to moderate the effect that the distance
// of travel has on the overall snap duration.
    fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= 0.3f * Math.PI.toFloat() / 2.0f
        return Math.sin(f.toDouble()).toFloat()
    }
    /**
     * Like [View.scrollBy], but scroll smoothly instead of immediately.
     *
     * @param x the number of pixels to scroll by on the X axis
     * @param y the number of pixels to scroll by on the Y axis
     * @param velocity the velocity associated with a fling, if applicable. (0 otherwise)
     */
    /**
     * Like [View.scrollBy], but scroll smoothly instead of immediately.
     *
     * @param x the number of pixels to scroll by on the X axis
     * @param y the number of pixels to scroll by on the Y axis
     */
    @JvmOverloads
    fun smoothScrollTo(x: Int, y: Int, velocity: Int = 0) {
        var velocity = velocity
        if (childCount == 0) { // Nothing to do.
            setScrollingCacheEnabled(false)
            return
        }
        val sx: Int
        val wasScrolling = mScroller != null && !mScroller!!.isFinished
        if (wasScrolling) { // We're in the middle of a previously initiated scrolling. Check to see
            // whether that scrolling has actually started (if we always call getStartX
            // we can get a stale value from the scroller if it hadn't yet had its first
            // computeScrollOffset call) to decide what is the current scrolling position.
            sx = if (mIsScrollStarted) mScroller!!.currX else mScroller!!.startX
            // And abort the current scrolling.
            mScroller!!.abortAnimation()
            setScrollingCacheEnabled(false)
        } else {
            sx = scrollX
        }
        val sy = scrollY
        val dx = x - sx
        val dy = y - sy
        if (dx == 0 && dy == 0) {
            completeScroll(false)
            populate()
            setScrollState(SCROLL_STATE_IDLE)
            return
        }
        setScrollingCacheEnabled(true)
        setScrollState(SCROLL_STATE_SETTLING)
        val width = clientWidth
        val halfWidth = width / 2
        val distanceRatio = 1f.coerceAtMost(1.0f * abs(dx) / width)
        val distance = (halfWidth + halfWidth).toFloat() * distanceInfluenceForSnapDuration(distanceRatio)
        var duration: Int
        velocity = abs(min(3500, velocity))
        duration = if (velocity > 0) {
            4 * Math.round(1000 * abs(distance / velocity))
        } else {
            val pageWidth = width * mAdapter!!.getPageWidth(mCurItem)
            val pageDelta = Math.abs(dx).toFloat() / (pageWidth + mPageMargin)
            ((pageDelta + 1) * 140 * durationFactor).toInt()  // TODO shikleev 100
        }
        duration = max(100, min(duration, MAX_SETTLE_DURATION))
        // Reset the "scroll started" flag. It will be flipped to true in all places
        // where we call computeScrollOffset().
        mIsScrollStarted = false
        mScroller!!.startScroll(sx, sy, dx, dy, duration)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setDurationFactor(factor: Float = 1.0f) {
        durationFactor = factor
    }

    private fun addNewItem(position: Int, index: Int): ItemInfo {
        val ii = ItemInfo()
        ii.position = position
        ii.`object` = mAdapter!!.instantiateItem(this, position)
        ii.widthFactor = mAdapter!!.getPageWidth(position)
        if (index < 0 || index >= mItems.size) {
            mItems.add(ii)
        } else {
            mItems.add(index, ii)
        }
        return ii
    }

    fun dataSetChanged() { // This method only gets called if our observer is attached, so mAdapter is non-null.
        val adapterCount = mAdapter!!.count
        mExpectedAdapterCount = adapterCount
        var needPopulate = (mItems.size < mOffscreenPageLimit * 2 + 1
                && mItems.size < adapterCount)
        var newCurrItem = mCurItem
        var isUpdating = false
        var i = 0
        while (i < mItems.size) {
            val ii = mItems[i]
            val newPos = mAdapter!!.getItemPosition(ii.`object`!!)
            if (newPos == PagerAdapter.POSITION_UNCHANGED) {
                i++
                continue
            }
            if (newPos == PagerAdapter.POSITION_NONE) {
                mItems.removeAt(i)
                i--
                if (!isUpdating) {
                    mAdapter!!.startUpdate(this)
                    isUpdating = true
                }
                mAdapter!!.destroyItem(this, ii.position, ii.`object`!!)
                needPopulate = true
                if (mCurItem == ii.position) { // Keep the current item in the valid range
                    newCurrItem = Math.max(0, Math.min(mCurItem, adapterCount - 1))
                    needPopulate = true
                }
                i++
                continue
            }
            if (ii.position != newPos) {
                if (ii.position == mCurItem) { // Our current item changed position. Follow it.
                    newCurrItem = newPos
                }
                ii.position = newPos
                needPopulate = true
            }
            i++
        }
        if (isUpdating) {
            mAdapter!!.finishUpdate(this)
        }
        Collections.sort(mItems, COMPARATOR)
        if (needPopulate) { // Reset our known page widths; populate will recompute them.
            val childCount = childCount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val lp = child.layoutParams as LayoutParams
                if (!lp.isDecor) {
                    lp.widthFactor = 0f
                }
            }
            setCurrentItemInternal(newCurrItem, false, true)
            requestLayout()
        }
        dispatchOnNotifyDataChanged(adapterCount)
    }

    @JvmOverloads
    fun populate(newCurrentItem: Int = mCurItem) {
        var oldCurInfo: ItemInfo? = null
        if (mCurItem != newCurrentItem) {
            oldCurInfo = infoForPosition(mCurItem)
            mCurItem = newCurrentItem
        }
        if (mAdapter == null) {
            sortChildDrawingOrder()
            return
        }
        // Bail now if we are waiting to populate.  This is to hold off
        // on creating views from the time the user releases their finger to
        // fling to a new position until we have finished the scroll to
        // that position, avoiding glitches from happening at that point.
        if (mPopulatePending) {
            if (DEBUG) Log.i(TAG, "populate is pending, skipping for now...")
            sortChildDrawingOrder()
            return
        }
        // Also, don't populate until we are attached to a window.  This is to
        // avoid trying to populate before we have restored our view hierarchy
        // state and conflicting with what is restored.
        if (windowToken == null) {
            return
        }
        mAdapter!!.startUpdate(this)
        val pageLimit = mOffscreenPageLimit
        val startPos = Math.max(0, mCurItem - pageLimit)
        val N = mAdapter!!.count
        val endPos = Math.min(N - 1, mCurItem + pageLimit)
        if (N != mExpectedAdapterCount) {
            val resName: String
            resName = try {
                resources.getResourceName(id)
            } catch (e: NotFoundException) {
                Integer.toHexString(id)
            }
            throw IllegalStateException("The application's PagerAdapter changed the adapter's"
                    + " contents without calling PagerAdapter#notifyDataSetChanged!"
                    + " Expected adapter item count: " + mExpectedAdapterCount + ", found: " + N
                    + " Pager id: " + resName
                    + " Pager class: " + javaClass
                    + " Problematic adapter: " + mAdapter?.javaClass)
        }
        // Locate the currently focused item or add it if needed.
        var curIndex = -1
        var curItem: ItemInfo? = null
        curIndex = 0
        while (curIndex < mItems.size) {
            val ii = mItems[curIndex]
            if (ii.position >= mCurItem) {
                if (ii.position == mCurItem) curItem = ii
                break
            }
            curIndex++
        }
        if (curItem == null && N > 0) {
            curItem = addNewItem(mCurItem, curIndex)
        }
        // Fill 3x the available width or up to the number of offscreen
        // pages requested to either side, whichever is larger.
        // If we have no current item we have no work to do.
        if (curItem != null) {
            var extraWidthLeft = 0f
            var itemIndex = curIndex - 1
            var ii = if (itemIndex >= 0) mItems[itemIndex] else null
            val clientWidth = clientWidth
            val leftWidthNeeded: Float = if (clientWidth <= 0) 0f else 2f - curItem.widthFactor + paddingLeft.toFloat() / clientWidth.toFloat()
            for (pos in mCurItem - 1 downTo 0) {
                if (extraWidthLeft >= leftWidthNeeded && pos < startPos) {
                    if (ii == null) {
                        break
                    }
                    if (pos == ii.position && !ii.scrolling) {
                        mItems.removeAt(itemIndex)
                        mAdapter!!.destroyItem(this, pos, ii.`object`!!)
                        if (DEBUG) {
                            Log.i(TAG, "populate() - destroyItem() with pos: " + pos
                                    + " view: " + ii.`object` as View?)
                        }
                        itemIndex--
                        curIndex--
                        ii = if (itemIndex >= 0) mItems[itemIndex] else null
                    }
                } else if (ii != null && pos == ii.position) {
                    extraWidthLeft += ii.widthFactor
                    itemIndex--
                    ii = if (itemIndex >= 0) mItems[itemIndex] else null
                } else {
                    ii = addNewItem(pos, itemIndex + 1)
                    extraWidthLeft += ii.widthFactor
                    curIndex++
                    ii = if (itemIndex >= 0) mItems[itemIndex] else null
                }
            }
            var extraWidthRight = curItem.widthFactor
            itemIndex = curIndex + 1
            if (extraWidthRight < 2f) {
                ii = if (itemIndex < mItems.size) mItems[itemIndex] else null
                val rightWidthNeeded: Float = if (clientWidth <= 0) 0f else paddingRight.toFloat() / clientWidth.toFloat() + 2f
                for (pos in mCurItem + 1 until N) {
                    if (extraWidthRight >= rightWidthNeeded && pos > endPos) {
                        if (ii == null) {
                            break
                        }
                        if (pos == ii.position && !ii.scrolling) {
                            mItems.removeAt(itemIndex)
                            mAdapter!!.destroyItem(this, pos, ii.`object`!!)
                            if (DEBUG) {
                                Log.i(TAG, "populate() - destroyItem() with pos: " + pos
                                        + " view: " + ii.`object` as View?)
                            }
                            ii = if (itemIndex < mItems.size) mItems[itemIndex] else null
                        }
                    } else if (ii != null && pos == ii.position) {
                        extraWidthRight += ii.widthFactor
                        itemIndex++
                        ii = if (itemIndex < mItems.size) mItems[itemIndex] else null
                    } else {
                        ii = addNewItem(pos, itemIndex)
                        itemIndex++
                        extraWidthRight += ii.widthFactor
                        ii = if (itemIndex < mItems.size) mItems[itemIndex] else null
                    }
                }
            }
            calculatePageOffsets(curItem, curIndex, oldCurInfo)
            mAdapter!!.setPrimaryItem(this, mCurItem, curItem.`object`!!)
        }
        if (DEBUG) {
            Log.i(TAG, "Current page list:")
            for (i in mItems.indices) {
                Log.i(TAG, "#" + i + ": page " + mItems[i].position)
            }
        }
        mAdapter!!.finishUpdate(this)
        // Check width measurement of current pages and drawing sort order.
        // Update LayoutParams as needed.
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            lp.childIndex = i
            if (!lp.isDecor && lp.widthFactor == 0f) { // 0 means requery the adapter for this, it doesn't have a valid width.
                val ii = infoForChild(child)
                if (ii != null) {
                    lp.widthFactor = ii.widthFactor
                    lp.position = ii.position
                }
            }
        }
        sortChildDrawingOrder()
        if (hasFocus()) {
            val currentFocused = findFocus()
            var ii = currentFocused?.let { infoForAnyChild(it) }
            if (ii == null || ii.position != mCurItem) {
                for (i in 0 until getChildCount()) {
                    val child = getChildAt(i)
                    ii = infoForChild(child)
                    if (ii != null && ii.position == mCurItem) {
                        if (child.requestFocus(View.FOCUS_FORWARD)) {
                            break
                        }
                    }
                }
            }
        }
    }

    private fun sortChildDrawingOrder() {
        if (mDrawingOrder != DRAW_ORDER_DEFAULT) {
            if (mDrawingOrderedChildren == null) {
                mDrawingOrderedChildren = ArrayList()
            } else {
                mDrawingOrderedChildren!!.clear()
            }
            val childCount = childCount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                mDrawingOrderedChildren!!.add(child)
            }
            Collections.sort(mDrawingOrderedChildren, sPositionComparator)
        }
    }

    private fun calculatePageOffsets(curItem: ItemInfo, curIndex: Int, oldCurInfo: ItemInfo?) {
        val N = mAdapter!!.count
        val width = clientWidth
        val marginOffset: Float = if (width > 0) mPageMargin.toFloat() / width else 0f
        // Fix up offsets for later layout.
        if (oldCurInfo != null) {
            val oldCurPosition = oldCurInfo.position
            // Base offsets off of oldCurInfo.
            if (oldCurPosition < curItem.position) {
                var itemIndex = 0
                var ii: ItemInfo? = null
                var offset = oldCurInfo.offset + oldCurInfo.widthFactor + marginOffset
                var pos = oldCurPosition + 1
                while (pos <= curItem.position && itemIndex < mItems.size) {
                    ii = mItems[itemIndex]
                    while (pos > ii!!.position && itemIndex < mItems.size - 1) {
                        itemIndex++
                        ii = mItems[itemIndex]
                    }
                    while (pos < ii.position) { // We don't have an item populated for this,
                        // ask the adapter for an offset.
                        offset += mAdapter!!.getPageWidth(pos) + marginOffset
                        pos++
                    }
                    ii.offset = offset
                    offset += ii.widthFactor + marginOffset
                    pos++
                }
            } else if (oldCurPosition > curItem.position) {
                var itemIndex = mItems.size - 1
                var ii: ItemInfo? = null
                var offset = oldCurInfo.offset
                var pos = oldCurPosition - 1
                while (pos >= curItem.position && itemIndex >= 0) {
                    ii = mItems[itemIndex]
                    while (pos < ii!!.position && itemIndex > 0) {
                        itemIndex--
                        ii = mItems[itemIndex]
                    }
                    while (pos > ii.position) { // We don't have an item populated for this,
                        // ask the adapter for an offset.
                        offset -= mAdapter!!.getPageWidth(pos) + marginOffset
                        pos--
                    }
                    offset -= ii.widthFactor + marginOffset
                    ii.offset = offset
                    pos--
                }
            }
        }
        // Base all offsets off of curItem.
        val itemCount = mItems.size
        var offset = curItem.offset
        var pos = curItem.position - 1
        mFirstOffset = if (curItem.position == 0) curItem.offset else -Float.MAX_VALUE
        mLastOffset = if (curItem.position == N - 1) curItem.offset + curItem.widthFactor - 1 else Float.MAX_VALUE
        // Previous pages
        run {
            var i = curIndex - 1
            while (i >= 0) {
                val ii = mItems[i]
                while (pos > ii.position) {
                    offset -= mAdapter!!.getPageWidth(pos--) + marginOffset
                }
                offset -= ii.widthFactor + marginOffset
                ii.offset = offset
                if (ii.position == 0) mFirstOffset = offset
                i--
                pos--
            }
        }
        offset = curItem.offset + curItem.widthFactor + marginOffset
        pos = curItem.position + 1
        // Next pages
        var i = curIndex + 1
        while (i < itemCount) {
            val ii = mItems[i]
            while (pos < ii.position) {
                offset += mAdapter!!.getPageWidth(pos++) + marginOffset
            }
            if (ii.position == N - 1) {
                mLastOffset = offset + ii.widthFactor - 1
            }
            ii.offset = offset
            offset += ii.widthFactor + marginOffset
            i++
            pos++
        }
        mNeedCalculatePageOffsets = false
    }

    /**
     * This is the persistent state that is saved by ViewPager.  Only needed
     * if you are creating a sublass of ViewPager that must save its own
     * state, in which case it should implement a subclass of this which
     * contains that state.
     */
    class SavedState : AbsSavedState {
        var position = 0
        var adapterState: Parcelable? = null
        var loader: ClassLoader? = null

        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(position)
            out.writeParcelable(adapterState, flags)
        }

        override fun toString(): String {
            return ("FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}")
        }

        internal constructor(`in`: Parcel, loader: ClassLoader?) : super(`in`, loader) {
            var loader = loader
            if (loader == null) {
                loader = javaClass.classLoader
            }
            position = `in`.readInt()
            adapterState = `in`.readParcelable(loader)
            this.loader = loader
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : ClassLoaderCreator<SavedState> {
                override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(`in`, loader)
                }

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`, null)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState!!)
        ss.position = mCurItem
        if (mAdapter != null) {
            ss.adapterState = mAdapter!!.saveState()
        }
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        val ss = state
        super.onRestoreInstanceState(ss.superState)
        if (mAdapter != null) {
            mAdapter!!.restoreState(ss.adapterState, ss.loader)
            setCurrentItemInternal(ss.position, false, true)
        } else {
            mRestoredCurItem = ss.position
            mRestoredAdapterState = ss.adapterState
            mRestoredClassLoader = ss.loader
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        var params = params
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params)
        }
        val lp = params as LayoutParams
        // Any views added via inflation should be classed as part of the decor
        lp.isDecor = lp.isDecor or isDecorView(child)
        if (mInLayout) {
            check(!(lp != null && lp.isDecor)) { "Cannot add pager decor view during layout" }
            lp.needsMeasure = true
            addViewInLayout(child, index, params)
        } else {
            super.addView(child, index, params)
        }
        if (USE_CACHE) {
            if (child.visibility != View.GONE) {
                child.isDrawingCacheEnabled = mScrollingCacheEnabled
            } else {
                child.isDrawingCacheEnabled = false
            }
        }
    }

    override fun removeView(view: View) {
        if (mInLayout) {
            removeViewInLayout(view)
        } else {
            super.removeView(view)
        }
    }

    fun infoForChild(child: View?): ItemInfo? {
        for (i in mItems.indices) {
            val ii = mItems[i]
            if (mAdapter!!.isViewFromObject(child!!, ii.`object`!!)) {
                return ii
            }
        }
        return null
    }

    fun infoForAnyChild(child: View): ItemInfo? {
        var child = child
        var parent: ViewParent?
        while (child.parent.also { parent = it } !== this) {
            if (parent == null || parent !is View) {
                return null
            }
            child = parent as View
        }
        return infoForChild(child)
    }

    fun infoForPosition(position: Int): ItemInfo? {
        for (i in mItems.indices) {
            val ii = mItems[i]
            if (ii.position == position) {
                return ii
            }
        }
        return null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mFirstLayout = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { // For simple implementation, our internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view.  We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec),
                View.getDefaultSize(0, heightMeasureSpec))
        val measuredWidth = measuredWidth
        val maxGutterSize = measuredWidth / 10
        mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize)
        // Children are just made to fill our space.
        var childWidthSize = measuredWidth - paddingLeft - paddingRight
        var childHeightSize = measuredHeight - paddingTop - paddingBottom
        /*
         * Make sure all children have been properly measured. Decor views first.
         * Right now we cheat and make this less complicated by assuming decor
         * views won't intersect. We will pin to edges based on gravity.
         */
        var size = childCount
        for (i in 0 until size) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams
                if (lp != null && lp.isDecor) {
                    val hgrav = lp.gravity and Gravity.HORIZONTAL_GRAVITY_MASK
                    val vgrav = lp.gravity and Gravity.VERTICAL_GRAVITY_MASK
                    var widthMode = MeasureSpec.AT_MOST
                    var heightMode = MeasureSpec.AT_MOST
                    val consumeVertical = vgrav == Gravity.TOP || vgrav == Gravity.BOTTOM
                    val consumeHorizontal = hgrav == Gravity.LEFT || hgrav == Gravity.RIGHT
                    if (consumeVertical) {
                        widthMode = MeasureSpec.EXACTLY
                    } else if (consumeHorizontal) {
                        heightMode = MeasureSpec.EXACTLY
                    }
                    var widthSize = childWidthSize
                    var heightSize = childHeightSize
                    if (lp.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                        widthMode = MeasureSpec.EXACTLY
                        if (lp.width != ViewGroup.LayoutParams.MATCH_PARENT) {
                            widthSize = lp.width
                        }
                    }
                    if (lp.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                        heightMode = MeasureSpec.EXACTLY
                        if (lp.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                            heightSize = lp.height
                        }
                    }
                    val widthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode)
                    val heightSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode)
                    child.measure(widthSpec, heightSpec)
                    if (consumeVertical) {
                        childHeightSize -= child.measuredHeight
                    } else if (consumeHorizontal) {
                        childWidthSize -= child.measuredWidth
                    }
                }
            }
        }
        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY)
        // Make sure we have created all fragments that we need to have shown.
        mInLayout = true
        populate()
        mInLayout = false
        // Page views next.
        size = childCount
        for (i in 0 until size) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                if (DEBUG) {
                    Log.v(TAG, "Measuring #$i $child: $mChildWidthMeasureSpec")
                }
                val lp = child.layoutParams as LayoutParams
                if (lp == null || !lp.isDecor) {
                    val widthSpec = MeasureSpec.makeMeasureSpec(
                            (childWidthSize * lp.widthFactor).toInt(), MeasureSpec.EXACTLY)
                    child.measure(widthSpec, mChildHeightMeasureSpec)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Make sure scroll position is set correctly.
        if (w != oldw) {
            recomputeScrollPosition(w, oldw, mPageMargin, mPageMargin)
        }
    }

    private fun recomputeScrollPosition(width: Int, oldWidth: Int, margin: Int, oldMargin: Int) {
        if (oldWidth > 0 && !mItems.isEmpty()) {
            if (!mScroller!!.isFinished) {
                mScroller!!.finalX = currentItem * clientWidth
            } else {
                val widthWithMargin = width - paddingLeft - paddingRight + margin
                val oldWidthWithMargin = (oldWidth - paddingLeft - paddingRight
                        + oldMargin)
                val xpos = scrollX
                val pageOffset = xpos.toFloat() / oldWidthWithMargin
                val newOffsetPixels = (pageOffset * widthWithMargin).toInt()
                scrollTo(newOffsetPixels, scrollY)
            }
        } else {
            val ii = infoForPosition(mCurItem)
            val scrollOffset: Float = if (ii != null) Math.min(ii.offset, mLastOffset) else 0f
            val scrollPos = (scrollOffset * (width - paddingLeft - paddingRight)).toInt()
            if (scrollPos != scrollX) {
                completeScroll(false)
                scrollTo(scrollPos, scrollY)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val width = r - l
        val height = b - t
        var paddingLeft = paddingLeft
        var paddingTop = paddingTop
        var paddingRight = paddingRight
        var paddingBottom = paddingBottom
        val scrollX = scrollX
        var decorCount = 0
        // First pass - decor views. We need to do this in two passes so that
        // we have the proper offsets for non-decor views later.
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams
                var childLeft = 0
                var childTop = 0
                if (lp.isDecor) {
                    val hgrav = lp.gravity and Gravity.HORIZONTAL_GRAVITY_MASK
                    val vgrav = lp.gravity and Gravity.VERTICAL_GRAVITY_MASK
                    when (hgrav) {
                        Gravity.LEFT -> {
                            childLeft = paddingLeft
                            paddingLeft += child.measuredWidth
                        }
                        Gravity.CENTER_HORIZONTAL -> childLeft = Math.max((width - child.measuredWidth) / 2,
                                paddingLeft)
                        Gravity.RIGHT -> {
                            childLeft = width - paddingRight - child.measuredWidth
                            paddingRight += child.measuredWidth
                        }
                        else -> childLeft = paddingLeft
                    }
                    when (vgrav) {
                        Gravity.TOP -> {
                            childTop = paddingTop
                            paddingTop += child.measuredHeight
                        }
                        Gravity.CENTER_VERTICAL -> childTop = Math.max((height - child.measuredHeight) / 2,
                                paddingTop)
                        Gravity.BOTTOM -> {
                            childTop = height - paddingBottom - child.measuredHeight
                            paddingBottom += child.measuredHeight
                        }
                        else -> childTop = paddingTop
                    }
                    childLeft += scrollX
                    child.layout(childLeft, childTop,
                            childLeft + child.measuredWidth,
                            childTop + child.measuredHeight)
                    decorCount++
                }
            }
        }
        val childWidth = width - paddingLeft - paddingRight
        // Page views. Do this once we have the right padding offsets from above.
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams
                if (!lp.isDecor && infoForChild(child) != null) {
                    val ii = infoForChild(child)
                    val loff = (childWidth * ii!!.offset).toInt()
                    val childLeft = paddingLeft + loff
                    val childTop = paddingTop
                    if (lp.needsMeasure) { // This was added during layout and needs measurement.
                        // Do it now that we know what we're working with.
                        lp.needsMeasure = false
                        val widthSpec = MeasureSpec.makeMeasureSpec(
                                (childWidth * lp.widthFactor).toInt(),
                                MeasureSpec.EXACTLY)
                        val heightSpec = MeasureSpec.makeMeasureSpec(
                                (height - paddingTop - paddingBottom),
                                MeasureSpec.EXACTLY)
                        child.measure(widthSpec, heightSpec)
                    }
                    if (DEBUG) {
                        Log.v(TAG, "Positioning #" + i + " " + child + " f=" + ii.`object`
                                + ":" + childLeft + "," + childTop + " " + child.measuredWidth
                                + "x" + child.measuredHeight)
                    }
                    child.layout(childLeft, childTop,
                            childLeft + child.measuredWidth,
                            childTop + child.measuredHeight)
                }
            }
        }
        mTopPageBounds = paddingTop
        mBottomPageBounds = height - paddingBottom
        mDecorChildCount = decorCount
        if (mFirstLayout) {
            scrollToItem(mCurItem, false, 0, false)
        }
        mFirstLayout = false
    }

    override fun computeScroll() {
        mIsScrollStarted = true
        if (!mScroller!!.isFinished && mScroller!!.computeScrollOffset()) {
            val oldX = scrollX
            val oldY = scrollY
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            if (oldX != x || oldY != y) {
                scrollTo(x, y)
                if (!pageScrolled(x)) {
                    mScroller!!.abortAnimation()
                    scrollTo(0, y)
                }
            }
            // Keep on drawing until the animation has finished.
            ViewCompat.postInvalidateOnAnimation(this)
            return
        }
        // Done with scroll, clean up state.
        completeScroll(true)
    }

    private fun pageScrolled(xpos: Int): Boolean {
        if (mItems.size == 0) {
            if (mFirstLayout) { // If we haven't been laid out yet, we probably just haven't been populated yet.
            // Let's skip this call since it doesn't make sense in this state
                return false
            }
            mCalledSuper = false
            onPageScrolled(0, 0f, 0)
            check(mCalledSuper) { "onPageScrolled did not call superclass implementation" }
            return false
        }
        val ii = infoForCurrentScrollPosition()
        val width = clientWidth
        val widthWithMargin = width + mPageMargin
        val marginOffset = mPageMargin.toFloat() / width
        val currentPage = ii!!.position
        val pageOffset = ((xpos.toFloat() / width - ii.offset)
                / (ii.widthFactor + marginOffset))
        val offsetPixels = (pageOffset * widthWithMargin).toInt()
        mCalledSuper = false
        onPageScrolled(currentPage, pageOffset, offsetPixels)
        check(mCalledSuper) { "onPageScrolled did not call superclass implementation" }
        return true
    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     * If you override this method you must call through to the superclass implementation
     * (e.g. super.onPageScrolled(position, offset, offsetPixels)) before onPageScrolled
     * returns.
     *
     * @param position Position index of the first page currently being displayed.
     * Page position+1 will be visible if positionOffset is nonzero.
     * @param offset Value from [0, 1) indicating the offset from the page at position.
     * @param offsetPixels Value in pixels indicating the offset from position.
     */
    @CallSuper
    protected fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) { // Offset any decor views if needed - keep them on-screen at all times.
        if (mDecorChildCount > 0) {
            val scrollX = scrollX
            var paddingLeft = paddingLeft
            var paddingRight = paddingRight
            val width = width
            val childCount = childCount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val lp = child.layoutParams as LayoutParams
                if (!lp.isDecor) continue
                val hgrav = lp.gravity and Gravity.HORIZONTAL_GRAVITY_MASK
                var childLeft = 0
                when (hgrav) {
                    Gravity.LEFT -> {
                        childLeft = paddingLeft
                        paddingLeft += child.width
                    }
                    Gravity.CENTER_HORIZONTAL -> childLeft = Math.max((width - child.measuredWidth) / 2,
                            paddingLeft)
                    Gravity.RIGHT -> {
                        childLeft = width - paddingRight - child.measuredWidth
                        paddingRight += child.measuredWidth
                    }
                    else -> childLeft = paddingLeft
                }
                childLeft += scrollX
                val childOffset = childLeft - child.left
                if (childOffset != 0) {
                    child.offsetLeftAndRight(childOffset)
                }
            }
        }
        dispatchOnPageScrolled(position, offset, offsetPixels)
        if (mPageTransformer != null) {
            val scrollX = scrollX
            val childCount = childCount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val lp = child.layoutParams as LayoutParams
                if (lp.isDecor) continue
                val transformPos = (child.left - scrollX).toFloat() / clientWidth
                if (isChangingChildVisibility) {
                    setChangingChildVisibility(child, transformPos)
                }
                mPageTransformer!!.transformPage(child, transformPos)
            }
        }
        mCalledSuper = true
    }

    private fun setChangingChildVisibility(child: View, position: Float) {
        when {
            position <= -0.99 -> {
                child.invisible()
            }
            position > 0 && position < 0.99 -> {
                child.visible()
            }
            position <= 0 -> {
                child.visible()
            }
            else -> {
                child.invisible()
            }
        }
    }

    private fun dispatchOnPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onPageScrolled(position, offset, offsetPixels)
        }
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners!!.size
            while (i < z) {
                val listener = mOnPageChangeListeners!![i]
                listener.onPageScrolled(position, offset, offsetPixels)
                i++
            }
        }
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onPageScrolled(position, offset, offsetPixels)
        }
    }

    private fun dispatchOnNotifyDataChanged(itemCount: Int) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onNotifyDataChanged(itemCount)
        }
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners!!.size
            while (i < z) {
                val listener = mOnPageChangeListeners!![i]
                listener.onNotifyDataChanged(itemCount)
                i++
            }
        }
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onNotifyDataChanged(itemCount)
        }
    }

    private fun dispatchOnPageSelected(position: Int) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onPageSelected(position)
        }
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners!!.size
            while (i < z) {
                val listener = mOnPageChangeListeners!![i]
                listener.onPageSelected(position)
                i++
            }
        }
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onPageSelected(position)
        }
    }

    private fun dispatchOnScrollStateChanged(state: Int) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onPageScrollStateChanged(state)
        }
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners!!.size
            while (i < z) {
                val listener = mOnPageChangeListeners!![i]
                listener.onPageScrollStateChanged(state)
                i++
            }
        }
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onPageScrollStateChanged(state)
        }
    }

    private fun completeScroll(postEvents: Boolean) {
        var needPopulate = mScrollState == SCROLL_STATE_SETTLING
        if (needPopulate) { // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false)
            val wasScrolling = !mScroller!!.isFinished
            if (wasScrolling) {
                mScroller!!.abortAnimation()
                val oldX = scrollX
                val oldY = scrollY
                val x = mScroller!!.currX
                val y = mScroller!!.currY
                if (oldX != x || oldY != y) {
                    scrollTo(x, y)
                    if (x != oldX) {
                        pageScrolled(x)
                    }
                }
            }
        }
        mPopulatePending = false
        for (i in mItems.indices) {
            val ii = mItems[i]
            if (ii.scrolling) {
                needPopulate = true
                ii.scrolling = false
            }
        }
        if (needPopulate) {
            if (postEvents) {
                ViewCompat.postOnAnimation(this, mEndScrollRunnable)
            } else {
                mEndScrollRunnable.run()
            }
        }
    }

    private fun isGutterDrag(x: Float, dx: Float): Boolean {
        return x < mGutterSize && dx > 0 || x > width - mGutterSize && dx < 0
    }

    private fun enableLayers(enable: Boolean) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val layerType = if (enable) mPageTransformerLayerType else View.LAYER_TYPE_NONE
            getChildAt(i).setLayerType(layerType, null)
        }
    }

    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        if (this.direction === SwipeDirection.ALL) return true

        if (direction === SwipeDirection.NONE)
            return false

        if (event.action == MotionEvent.ACTION_DOWN) {
            initialXValue = event.x
            return true
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            tryCatch {
                val diffX = event.x - initialXValue
                if (diffX > 0 && direction === SwipeDirection.RIGHT) {
                    return false
                } else if (diffX < 0 && direction === SwipeDirection.LEFT) {
                    return false
                }
            }

        }
        return true
    }

    fun setAllowedSwipeDirection(direction: SwipeDirection) {
        this.direction = direction
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean { /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */
        if (!isSwipeAllowed(ev)) return false

        val action = ev.action and MotionEvent.ACTION_MASK
        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) { // Release the drag.
            if (DEBUG) Log.v(TAG, "Intercept done!")
            resetTouch()
            return false
        }
        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                if (DEBUG) Log.v(TAG, "Intercept returning true!")
                return true
            }
            if (mIsUnableToDrag) {
                if (DEBUG) Log.v(TAG, "Intercept returning false!")
                return false
            }
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
                /*
                 * Locally do absolute value. mLastMotionY is set to the y value
                 * of the down event.
                 */
                val activePointerId = mActivePointerId
                if (activePointerId == INVALID_POINTER) { // If we don't have a valid id, the touch down wasn't on content.
//                    return false  // TODO BREAK
                }
                val pointerIndex = ev.findPointerIndex(activePointerId)
                val x = ev.getX(pointerIndex)
                val dx = x - mLastMotionX
                val xDiff = Math.abs(dx)
                val y = ev.getY(pointerIndex)
                val yDiff = Math.abs(y - mInitialMotionY)
                if (DEBUG) Log.v(TAG, "Moved x to $x,$y diff=$xDiff,$yDiff")
                if (dx != 0f && !isGutterDrag(mLastMotionX, dx)
                        && canScroll(this, false, dx.toInt(), x.toInt(), y.toInt())) { // Nested view has scrollable area under this point. Let it be handled there.
                    mLastMotionX = x
                    mLastMotionY = y
                    mIsUnableToDrag = true
                    return false
                }
                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                    if (DEBUG) Log.v(TAG, "Starting drag!")
                    mIsBeingDragged = true
                    requestParentDisallowInterceptTouchEvent(true)
                    setScrollState(SCROLL_STATE_DRAGGING)
                    mLastMotionX = if (dx > 0) mInitialMotionX + mTouchSlop else mInitialMotionX - mTouchSlop
                    mLastMotionY = y
                    setScrollingCacheEnabled(true)
                } else if (yDiff > mTouchSlop) { // The finger has moved enough in the vertical
                    // direction to be counted as a drag...  abort
                    // any attempt to drag horizontally, to work correctly
                    // with children that have scrolling containers.
                    if (DEBUG) Log.v(TAG, "Starting unable to drag!")
                    mIsUnableToDrag = true
                }
                if (mIsBeingDragged) { // Scroll to follow the motion event
                    if (performDrag(x)) {
                        ViewCompat.postInvalidateOnAnimation(this)
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */mInitialMotionX = ev.x
                mLastMotionX = mInitialMotionX
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = ev.getPointerId(0)
                mIsUnableToDrag = false
                mIsScrollStarted = true
                mScroller!!.computeScrollOffset()
                if (mScrollState == SCROLL_STATE_SETTLING
                        && Math.abs(mScroller!!.finalX - mScroller!!.currX) > mCloseEnough) { // Let the user 'catch' the pager as it animates.
                    mScroller!!.abortAnimation()
                    mPopulatePending = false
                    populate()
                    mIsBeingDragged = true
                    requestParentDisallowInterceptTouchEvent(true)
                    setScrollState(SCROLL_STATE_DRAGGING)
                } else {
                    completeScroll(false)
                    mIsBeingDragged = false
                }
                if (DEBUG) {
                    Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY
                            + " mIsBeingDragged=" + mIsBeingDragged
                            + "mIsUnableToDrag=" + mIsUnableToDrag)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)
        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isFakeDragging) { // A fake drag is in progress already, ignore this real one
        // but still eat the touch events.
        // (It is likely that the user is multi-touching the screen.)
            return true
        }
        if (ev.action == MotionEvent.ACTION_DOWN && ev.edgeFlags != 0) { // Don't handle edge touches immediately -- they may actually belong to one of our
        // descendants.
            return false
        }
        if (mAdapter == null || mAdapter!!.count == 0) { // Nothing to present or scroll; nothing to touch.
            return false
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)
        val action = ev.action
        var needsInvalidate = false
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mScroller!!.abortAnimation()
                mPopulatePending = false
                populate()
                // Remember where the motion event started
                mInitialMotionX = ev.x
                mLastMotionX = mInitialMotionX
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsBeingDragged) {
                    val pointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (pointerIndex == -1) { // A child has consumed some touch events and put us into an inconsistent
                    // state.
                        needsInvalidate = resetTouch()
//                        return false // TODO BREAK
                    }
                    val x = ev.getX(pointerIndex)
                    val xDiff = Math.abs(x - mLastMotionX)
                    val y = ev.getY(pointerIndex)
                    val yDiff = Math.abs(y - mLastMotionY)
                    if (DEBUG) {
                        Log.v(TAG, "Moved x to $x,$y diff=$xDiff,$yDiff")
                    }
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        if (DEBUG) Log.v(TAG, "Starting drag!")
                        mIsBeingDragged = true
                        requestParentDisallowInterceptTouchEvent(true)
                        mLastMotionX = if (x - mInitialMotionX > 0) mInitialMotionX + mTouchSlop else mInitialMotionX - mTouchSlop
                        mLastMotionY = y
                        setScrollState(SCROLL_STATE_DRAGGING)
                        setScrollingCacheEnabled(true)
                        // Disallow Parent Intercept, just in case
                        val parent = parent
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                // Not else! Note that mIsBeingDragged can be set above.
                if (mIsBeingDragged) { // Scroll to follow the motion event
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    val x = ev.getX(activePointerIndex)
                    needsInvalidate = needsInvalidate or performDrag(x)
                }
            }
            MotionEvent.ACTION_UP -> if (mIsBeingDragged) {
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val initialVelocity = velocityTracker.getXVelocity(mActivePointerId).toInt()
                mPopulatePending = true
                val width = clientWidth
                val scrollX = scrollX
                val ii = infoForCurrentScrollPosition()
                val marginOffset = mPageMargin.toFloat() / width
                val currentPage = ii!!.position
                val pageOffset = ((scrollX.toFloat() / width - ii.offset)
                        / (ii.widthFactor + marginOffset))
                val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                val x = ev.getX(activePointerIndex)
                val totalDelta = (x - mInitialMotionX).toInt()
                val nextPage = determineTargetPage(currentPage, pageOffset, initialVelocity,
                        totalDelta)
                setCurrentItemInternal(nextPage, true, true, initialVelocity)
                needsInvalidate = resetTouch()
            }
            MotionEvent.ACTION_CANCEL -> if (mIsBeingDragged) {
                scrollToItem(mCurItem, true, 0, false)
                needsInvalidate = resetTouch()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                val x = ev.getX(index)
                mLastMotionX = x
                mActivePointerId = ev.getPointerId(index)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(ev)
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId))
            }
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
        return true
    }

    private fun resetTouch(): Boolean {
        val needsInvalidate: Boolean
        mActivePointerId = INVALID_POINTER
        endDrag()
        mLeftEdge!!.onRelease()
        mRightEdge!!.onRelease()
        needsInvalidate = mLeftEdge!!.isFinished || mRightEdge!!.isFinished
        return needsInvalidate
    }

    private fun requestParentDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    private fun performDrag(x: Float): Boolean {
        var needsInvalidate = false
        val deltaX = mLastMotionX - x
        mLastMotionX = x
        val oldScrollX = scrollX.toFloat()
        var scrollX = oldScrollX + deltaX
        val width = clientWidth
        var leftBound = width * mFirstOffset
        var rightBound = width * mLastOffset
        var leftAbsolute = true
        var rightAbsolute = true
        val firstItem = mItems[0]
        val lastItem = mItems[mItems.size - 1]
        if (firstItem.position != 0) {
            leftAbsolute = false
            leftBound = firstItem.offset * width
        }
        if (lastItem.position != mAdapter!!.count - 1) {
            rightAbsolute = false
            rightBound = lastItem.offset * width
        }
        if (scrollX < leftBound) {
            if (leftAbsolute) {
                val over = leftBound - scrollX
                mLeftEdge!!.onPull(Math.abs(over) / width)
                needsInvalidate = true
            }
            scrollX = leftBound
        } else if (scrollX > rightBound) {
            if (rightAbsolute) {
                val over = scrollX - rightBound
                mRightEdge!!.onPull(Math.abs(over) / width)
                needsInvalidate = true
            }
            scrollX = rightBound
        }
        // Don't lose the rounded component
        mLastMotionX += scrollX - scrollX.toInt()
        scrollTo(scrollX.toInt(), scrollY)
        pageScrolled(scrollX.toInt())
        return needsInvalidate
    }

    /**
     * @return Info about the page at the current scroll position.
     * This can be synthetic for a missing middle page; the 'object' field can be null.
     */
    private fun infoForCurrentScrollPosition(): ItemInfo? {
        val width = clientWidth
        val scrollOffset: Float = if (width > 0) scrollX.toFloat() / width else 0f
        val marginOffset: Float = if (width > 0) mPageMargin.toFloat() / width else 0f
        var lastPos = -1
        var lastOffset = 0f
        var lastWidth = 0f
        var first = true
        var lastItem: ItemInfo? = null
        var i = 0
        while (i < mItems.size) {
            var ii = mItems[i]
            var offset: Float
            if (!first && ii.position != lastPos + 1) { // Create a synthetic item for a missing page.
                ii = mTempItem
                ii.offset = lastOffset + lastWidth + marginOffset
                ii.position = lastPos + 1
                ii.widthFactor = mAdapter!!.getPageWidth(ii.position)
                i--
            }
            offset = ii.offset
            val leftBound = offset
            val rightBound = offset + ii.widthFactor + marginOffset
            if (first || scrollOffset >= leftBound) {
                if (scrollOffset < rightBound || i == mItems.size - 1) {
                    return ii
                }
            } else {
                return lastItem
            }
            first = false
            lastPos = ii.position
            lastOffset = offset
            lastWidth = ii.widthFactor
            lastItem = ii
            i++
        }
        return lastItem
    }

    private fun determineTargetPage(currentPage: Int, pageOffset: Float, velocity: Int, deltaX: Int): Int {
        var targetPage: Int
        targetPage = if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            if (velocity > 0) currentPage else currentPage + 1
        } else {
            val truncator = if (currentPage >= mCurItem) 0.4f else 0.6f
            currentPage + (pageOffset + truncator).toInt()
        }
        if (mItems.size > 0) {
            val firstItem = mItems[0]
            val lastItem = mItems[mItems.size - 1]
            // Only let the user target pages we have items for
            targetPage = Math.max(firstItem.position, Math.min(targetPage, lastItem.position))
        }
        return targetPage
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        var needsInvalidate = false
        val overScrollMode = overScrollMode
        if (overScrollMode == View.OVER_SCROLL_ALWAYS
                || overScrollMode == View.OVER_SCROLL_IF_CONTENT_SCROLLS && mAdapter != null && mAdapter!!.count > 1) {
            if (!mLeftEdge!!.isFinished) {
                val restoreCount = canvas.save()
                val height = height - paddingTop - paddingBottom
                val width = width
                canvas.rotate(270f)
                canvas.translate(-height + paddingTop.toFloat(), mFirstOffset * width)
                mLeftEdge!!.setSize(height, width)
                needsInvalidate = needsInvalidate or mLeftEdge!!.draw(canvas)
                canvas.restoreToCount(restoreCount)
            }
            if (!mRightEdge!!.isFinished) {
                val restoreCount = canvas.save()
                val width = width
                val height = height - paddingTop - paddingBottom
                canvas.rotate(90f)
                canvas.translate(-paddingTop.toFloat(), -(mLastOffset + 1) * width)
                mRightEdge!!.setSize(height, width)
                needsInvalidate = needsInvalidate or mRightEdge!!.draw(canvas)
                canvas.restoreToCount(restoreCount)
            }
        } else {
            mLeftEdge!!.finish()
            mRightEdge!!.finish()
        }
        if (needsInvalidate) { // Keep animating
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the margin drawable between pages if needed.
        if (mPageMargin > 0 && mMarginDrawable != null && mItems.size > 0 && mAdapter != null) {
            val scrollX = scrollX
            val width = width
            val marginOffset = mPageMargin.toFloat() / width
            var itemIndex = 0
            var ii = mItems[0]
            var offset = ii.offset
            val itemCount = mItems.size
            val firstPos = ii.position
            val lastPos = mItems[itemCount - 1].position
            for (pos in firstPos until lastPos) {
                while (pos > ii.position && itemIndex < itemCount) {
                    ii = mItems[++itemIndex]
                }
                var drawAt: Float
                if (pos == ii.position) {
                    drawAt = (ii.offset + ii.widthFactor) * width
                    offset = ii.offset + ii.widthFactor + marginOffset
                } else {
                    val widthFactor = mAdapter!!.getPageWidth(pos)
                    drawAt = (offset + widthFactor) * width
                    offset += widthFactor + marginOffset
                }
                if (drawAt + mPageMargin > scrollX) {
                    mMarginDrawable!!.setBounds(Math.round(drawAt), mTopPageBounds,
                            Math.round(drawAt + mPageMargin), mBottomPageBounds)
                    mMarginDrawable!!.draw(canvas)
                }
                if (drawAt > scrollX + width) {
                    break // No more visible, no sense in continuing
                }
            }
        }
    }

    /**
     * Start a fake drag of the pager.
     *
     *
     * A fake drag can be useful if you want to synchronize the motion of the ViewPager
     * with the touch scrolling of another view, while still letting the ViewPager
     * control the snapping motion and fling behavior. (e.g. parallax-scrolling tabs.)
     * Call [.fakeDragBy] to simulate the actual drag motion. Call
     * [.endFakeDrag] to complete the fake drag and fling as necessary.
     *
     *
     * During a fake drag the ViewPager will ignore all touch events. If a real drag
     * is already in progress, this method will return false.
     *
     * @return true if the fake drag began successfully, false if it could not be started.
     *
     * @see .fakeDragBy
     * @see .endFakeDrag
     */
    fun beginFakeDrag(): Boolean {
        if (mIsBeingDragged) {
            return false
        }
        isFakeDragging = true
        setScrollState(SCROLL_STATE_DRAGGING)
        mLastMotionX = 0f
        mInitialMotionX = mLastMotionX
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker!!.clear()
        }
        val time = SystemClock.uptimeMillis()
        val ev = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        mVelocityTracker!!.addMovement(ev)
        ev.recycle()
        mFakeDragBeginTime = time
        return true
    }

    /**
     * End a fake drag of the pager.
     *
     * @see .beginFakeDrag
     * @see .fakeDragBy
     */
    fun endFakeDrag() {
        check(isFakeDragging) { "No fake drag in progress. Call beginFakeDrag first." }
        if (mAdapter != null) {
            val velocityTracker = mVelocityTracker
            velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
            val initialVelocity = velocityTracker.getXVelocity(mActivePointerId).toInt()
            mPopulatePending = true
            val width = clientWidth
            val scrollX = scrollX
            val ii = infoForCurrentScrollPosition()
            val currentPage = ii!!.position
            val pageOffset = (scrollX.toFloat() / width - ii.offset) / ii.widthFactor
            val totalDelta = (mLastMotionX - mInitialMotionX).toInt()
            val nextPage = determineTargetPage(currentPage, pageOffset, initialVelocity,
                    totalDelta)
            setCurrentItemInternal(nextPage, true, true, initialVelocity)
        }
        endDrag()
        isFakeDragging = false
    }

    /**
     * Fake drag by an offset in pixels. You must have called [.beginFakeDrag] first.
     *
     * @param xOffset Offset in pixels to drag by.
     * @see .beginFakeDrag
     * @see .endFakeDrag
     */
    fun fakeDragBy(xOffset: Float) {
        check(isFakeDragging) { "No fake drag in progress. Call beginFakeDrag first." }
        if (mAdapter == null) {
            return
        }
        mLastMotionX += xOffset
        val oldScrollX = scrollX.toFloat()
        var scrollX = oldScrollX - xOffset
        val width = clientWidth
        var leftBound = width * mFirstOffset
        var rightBound = width * mLastOffset
        val firstItem = mItems[0]
        val lastItem = mItems[mItems.size - 1]
        if (firstItem.position != 0) {
            leftBound = firstItem.offset * width
        }
        if (lastItem.position != mAdapter!!.count - 1) {
            rightBound = lastItem.offset * width
        }
        if (scrollX < leftBound) {
            scrollX = leftBound
        } else if (scrollX > rightBound) {
            scrollX = rightBound
        }
        // Don't lose the rounded component
        mLastMotionX += scrollX - scrollX.toInt()
        scrollTo(scrollX.toInt(), scrollY)
        pageScrolled(scrollX.toInt())
        // Synthesize an event for the VelocityTracker.
        val time = SystemClock.uptimeMillis()
        val ev = MotionEvent.obtain(mFakeDragBeginTime, time, MotionEvent.ACTION_MOVE,
                mLastMotionX, 0f, 0)
        mVelocityTracker!!.addMovement(ev)
        ev.recycle()
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) { // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
            if (mVelocityTracker != null) {
                mVelocityTracker!!.clear()
            }
        }
    }

    private fun endDrag() {
        mIsBeingDragged = false
        mIsUnableToDrag = false
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun setScrollingCacheEnabled(enabled: Boolean) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled
            if (USE_CACHE) {
                val size = childCount
                for (i in 0 until size) {
                    val child = getChildAt(i)
                    if (child.visibility != View.GONE) {
                        child.isDrawingCacheEnabled = enabled
                    }
                }
            }
        }
    }

    /**
     * Check if this ViewPager can be scrolled horizontally in a certain direction.
     *
     * @param direction Negative to check scrolling left, positive to check scrolling right.
     * @return Whether this ViewPager can be scrolled in the specified direction. It will always
     * return false if the specified direction is 0.
     */
    override fun canScrollHorizontally(direction: Int): Boolean {
        if (mAdapter == null) {
            return false
        }
        val width = clientWidth
        val scrollX = scrollX
        return if (direction < 0) {
            scrollX > (width * mFirstOffset).toInt()
        } else if (direction > 0) {
            scrollX < (width * mLastOffset).toInt()
        } else {
            false
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     * or just its children (false).
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val group = v
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = group.childCount
            // Count backwards - let topmost views consume scroll distance first.
            for (i in count - 1 downTo 0) { // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                val child = group.getChildAt(i)
                if (x + scrollX >= child.left && x + scrollX < child.right && y + scrollY >= child.top && y + scrollY < child.bottom && canScroll(child, true, dx, x + scrollX - child.left,
                                y + scrollY - child.top)) {
                    return true
                }
            }
        }
        return checkV && v.canScrollHorizontally(-dx)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean { // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event)
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    fun executeKeyEvent(event: KeyEvent): Boolean {
        var handled = false
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> handled = if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                    pageLeft()
                } else {
                    arrowScroll(View.FOCUS_LEFT)
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> handled = if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                    pageRight()
                } else {
                    arrowScroll(View.FOCUS_RIGHT)
                }
                KeyEvent.KEYCODE_TAB -> if (event.hasNoModifiers()) {
                    handled = arrowScroll(View.FOCUS_FORWARD)
                } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
                    handled = arrowScroll(View.FOCUS_BACKWARD)
                }
            }
        }
        return handled
    }

    /**
     * Handle scrolling in response to a left or right arrow click.
     *
     * @param direction The direction corresponding to the arrow key that was pressed. It should be
     * either [View.FOCUS_LEFT] or [View.FOCUS_RIGHT].
     * @return Whether the scrolling was handled successfully.
     */
    fun arrowScroll(direction: Int): Boolean {
        var currentFocused = findFocus()
        if (currentFocused === this) {
            currentFocused = null
        } else if (currentFocused != null) {
            var isChild = false
            var parent = currentFocused.parent
            while (parent is ViewGroup) {
                if (parent === this) {
                    isChild = true
                    break
                }
                parent = parent.getParent()
            }
            if (!isChild) { // This would cause the focus search down below to fail in fun ways.
                val sb = StringBuilder()
                sb.append(currentFocused.javaClass.simpleName)
                var parent = currentFocused.parent
                while (parent is ViewGroup) {
                    sb.append(" => ").append(parent.javaClass.simpleName)
                    parent = parent.getParent()
                }
                Log.e(TAG, "arrowScroll tried to find focus based on non-child "
                        + "current focused view " + sb.toString())
                currentFocused = null
            }
        }
        var handled = false
        val nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
                direction)
        if (nextFocused != null && nextFocused !== currentFocused) {
            if (direction == View.FOCUS_LEFT) { // If there is nothing to the left, or this is causing us to
                // jump to the right, then what we really want to do is page left.
                val nextLeft = getChildRectInPagerCoordinates(mTempRect, nextFocused).left
                val currLeft = getChildRectInPagerCoordinates(mTempRect, currentFocused).left
                handled = if (currentFocused != null && nextLeft >= currLeft) {
                    pageLeft()
                } else {
                    nextFocused.requestFocus()
                }
            } else if (direction == View.FOCUS_RIGHT) { // If there is nothing to the right, or this is causing us to
                // jump to the left, then what we really want to do is page right.
                val nextLeft = getChildRectInPagerCoordinates(mTempRect, nextFocused).left
                val currLeft = getChildRectInPagerCoordinates(mTempRect, currentFocused).left
                handled = if (currentFocused != null && nextLeft <= currLeft) {
                    pageRight()
                } else {
                    nextFocused.requestFocus()
                }
            }
        } else if (direction == View.FOCUS_LEFT || direction == View.FOCUS_BACKWARD) { // Trying to move left and nothing there; try to page.
            handled = pageLeft()
        } else if (direction == View.FOCUS_RIGHT || direction == View.FOCUS_FORWARD) { // Trying to move right and nothing there; try to page.
            handled = pageRight()
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction))
        }
        return handled
    }

    private fun getChildRectInPagerCoordinates(outRect: Rect, child: View?): Rect {
        var outRect: Rect? = outRect
        if (outRect == null) {
            outRect = Rect()
        }
        if (child == null) {
            outRect[0, 0, 0] = 0
            return outRect
        }
        outRect.left = child.left
        outRect.right = child.right
        outRect.top = child.top
        outRect.bottom = child.bottom
        var parent = child.parent
        while (parent is ViewGroup && parent !== this) {
            val group = parent
            outRect.left += group.left
            outRect.right += group.right
            outRect.top += group.top
            outRect.bottom += group.bottom
            parent = group.parent
        }
        return outRect
    }

    fun pageLeft(): Boolean {
        if (mCurItem > 0) {
            goToFragment(mCurItem - 1, true)
            return true
        }
        return false
    }

    fun pageRight(): Boolean {
        if (mAdapter != null && mCurItem < mAdapter!!.count - 1) {
            goToFragment(mCurItem + 1, true)
            return true
        }
        return false
    }

    /**
     * We only want the current page that is being shown to be focusable.
     */
    override fun addFocusables(views: ArrayList<View>, direction: Int, focusableMode: Int) {
        val focusableCount = views.size
        val descendantFocusability = descendantFocusability
        if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.visibility == View.VISIBLE) {
                    val ii = infoForChild(child)
                    if (ii != null && ii.position == mCurItem) {
                        child.addFocusables(views, direction, focusableMode)
                    }
                }
            }
        }
        // we add ourselves (if focusable) in all cases except for when we are
        // FOCUS_AFTER_DESCENDANTS and there are some descendants focusable.  this is
        // to avoid the focus search finding layouts when a more precise search
        // among the focusable children would be more interesting.
        if (descendantFocusability != FOCUS_AFTER_DESCENDANTS
                || focusableCount == views.size) { // No focusable descendants
            // Note that we can't call the superclass here, because it will
            // add all views in.  So we need to do the same thing View does.
            if (!isFocusable) {
                return
            }
            if (focusableMode and View.FOCUSABLES_TOUCH_MODE == View.FOCUSABLES_TOUCH_MODE && isInTouchMode && !isFocusableInTouchMode) {
                return
            }
            views.add(this)
        }
    }

    /**
     * We only want the current page that is being shown to be touchable.
     */
    override fun addTouchables(views: ArrayList<View>) { // Note that we don't call super.addTouchables(), which means that
        // we don't call View.addTouchables().  This is okay because a ViewPager
        // is itself not touchable.
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                val ii = infoForChild(child)
                if (ii != null && ii.position == mCurItem) {
                    child.addTouchables(views)
                }
            }
        }
    }

    /**
     * We only want the current page that is being shown to be focusable.
     */
    override fun onRequestFocusInDescendants(direction: Int,
                                             previouslyFocusedRect: Rect?): Boolean {
        val index: Int
        val increment: Int
        val end: Int
        val count = childCount
        if (direction and View.FOCUS_FORWARD != 0) {
            index = 0
            increment = 1
            end = count
        } else {
            index = count - 1
            increment = -1
            end = -1
        }
        var i = index
        while (i != end) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                val ii = infoForChild(child)
                if (ii != null && ii.position == mCurItem) {
                    if (child.requestFocus(direction, previouslyFocusedRect)) {
                        return true
                    }
                }
            }
            i += increment
        }
        return false
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean { // Dispatch scroll events from this ViewPager.
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return super.dispatchPopulateAccessibilityEvent(event)
        }
        // Dispatch all other accessibility events from the current page.
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                val ii = infoForChild(child)
                if (ii != null && ii.position == mCurItem && child.dispatchPopulateAccessibilityEvent(event)) {
                    return true
                }
            }
        }
        return false
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams()
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return generateDefaultLayoutParams()
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams && super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    internal inner class MyAccessibilityDelegate : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityEvent(host: View, event: AccessibilityEvent) {
            super.onInitializeAccessibilityEvent(host, event)
            event.className = FragmentNavigator::class.java.name
            event.isScrollable = canScroll()
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && mAdapter != null) {
                event.itemCount = mAdapter!!.count
                event.fromIndex = mCurItem
                event.toIndex = mCurItem
            }
        }

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.className = FragmentNavigator::class.java.name
            info.isScrollable = canScroll()
            if (canScrollHorizontally(1)) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD)
            }
            if (canScrollHorizontally(-1)) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD)
            }
        }

        override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
            if (super.performAccessibilityAction(host, action, args)) {
                return true
            }
            when (action) {
                AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD -> {
                    run {
                        if (canScrollHorizontally(1)) {
                            currentItem = mCurItem + 1
                            return true
                        }
                    }
                    return false
                }
                AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD -> {
                    run {
                        if (canScrollHorizontally(-1)) {
                            currentItem = mCurItem - 1
                            return true
                        }
                    }
                    return false
                }
            }
            return false
        }

        private fun canScroll(): Boolean {
            return mAdapter != null && mAdapter!!.count > 1
        }
    }

    private inner class PagerObserver internal constructor() : DataSetObserver() {
        override fun onChanged() {
            dataSetChanged()
        }

        override fun onInvalidated() {
            dataSetChanged()
        }
    }

    /**
     * Layout parameters that should be supplied for views added to a
     * ViewPager.
     */
    class LayoutParams : ViewGroup.LayoutParams {
        /**
         * true if this view is a decoration on the pager itself and not
         * a view supplied by the adapter.
         */
        var isDecor = false
        /**
         * Gravity setting for use on decor views only:
         * Where to position the view page within the overall ViewPager
         * container; constants are defined in [android.view.Gravity].
         */
        var gravity = 0
        /**
         * Width as a 0-1 multiplier of the measured pager width
         */
        var widthFactor = 0f
        /**
         * true if this view was added during layout and needs to be measured
         * before being positioned.
         */
        var needsMeasure = false
        /**
         * Adapter position this view is for if !isDecor
         */
        var position = 0
        /**
         * Current child index within the ViewPager that this view occupies
         */
        var childIndex = 0

        constructor() : super(MATCH_PARENT, MATCH_PARENT)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
            gravity = a.getInteger(0, Gravity.TOP)
            a.recycle()
        }
    }

    internal class ViewPositionComparator : Comparator<View> {
        override fun compare(lhs: View, rhs: View): Int {
            val llp = lhs.layoutParams as LayoutParams
            val rlp = rhs.layoutParams as LayoutParams
            return if (llp.isDecor != rlp.isDecor) {
                if (llp.isDecor) 1 else -1
            } else llp.position - rlp.position
        }
    }

    companion object {
        private const val TAG = "ViewPager"
        private const val DEBUG = false
        private const val USE_CACHE = false
        private const val DEFAULT_OFFSCREEN_PAGES = 1
        private const val MAX_SETTLE_DURATION = 600 // ms
        private const val MIN_DISTANCE_FOR_FLING = 25 // dips
        private const val DEFAULT_GUTTER_SIZE = 16 // dips
        private const val MIN_FLING_VELOCITY = 400 // dips
        val LAYOUT_ATTRS = intArrayOf(
                R.attr.layout_gravity
        )
        private val COMPARATOR = Comparator<ItemInfo> { lhs, rhs -> lhs.position - rhs.position }
        //        private val sInterpolator = Interpolator { t ->
//            var t = t
//            t -= 1.0f
//            t * t * t * t * t + 1.0f
//        }
        private val sInterpolator = DecelerateInterpolator(1.5f)
        /**
         * Sentinel value for no current active pointer.
         * Used by [.mActivePointerId].
         */
        private const val INVALID_POINTER = -1
        // If the pager is at least this close to its final position, complete the scroll
        // on touch down and let the user interact with the content inside instead of
        // "catching" the flinging pager.
        private const val CLOSE_ENOUGH = 2 // dp
        private const val DRAW_ORDER_DEFAULT = 0
        private const val DRAW_ORDER_FORWARD = 1
        private const val DRAW_ORDER_REVERSE = 2
        private val sPositionComparator = ViewPositionComparator()
        /**
         * Indicates that the pager is in an idle, settled state. The current page
         * is fully in view and no animation is in progress.
         */
        const val SCROLL_STATE_IDLE = 0
        /**
         * Indicates that the pager is currently being dragged by the user.
         */
        const val SCROLL_STATE_DRAGGING = 1
        /**
         * Indicates that the pager is in the process of settling to a final position.
         */
        const val SCROLL_STATE_SETTLING = 2

        private fun isDecorView(view: View): Boolean {
            val clazz: Class<*> = view.javaClass
            return clazz.getAnnotation(DecorView::class.java) != null
        }
    }
}