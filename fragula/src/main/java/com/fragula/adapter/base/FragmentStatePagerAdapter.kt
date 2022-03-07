package com.fragula.adapter.base

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Implementation of [androidx.viewpager.widget.PagerAdapter] that
 * uses a [Fragment] to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 *
 * This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * { FragmentPagerAdapter} at the cost of potentially more overhead when
 * switching between pages.
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter.
 *
 *
 * Here is an example implementation of a pager containing fragments of
 * lists:
 *
 * {@sample frameworks/support/samples/Support4Demos/src/main/java/com/example/android/supportv4/app/FragmentStatePagerSupport.java
 * *      complete}
 *
 *
 * The `R.layout.fragment_pager` resource of the top-level fragment is:
 *
 * {@sample frameworks/support/samples/Support4Demos/src/main/res/layout/fragment_pager.xml
 * *      complete}
 *
 *
 * The `R.layout.fragment_pager_list` resource containing each
 * individual fragment's layout is:
 *
 * {@sample frameworks/support/samples/Support4Demos/src/main/res/layout/fragment_pager_list.xml
 * *      complete}
 */
internal abstract class FragmentStatePagerAdapter
/**
 * Constructor for [androidx.fragment.app.FragmentStatePagerAdapter].
 *
 * If [.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT] is passed in, then only the current
 * Fragment is in the [Lifecycle.State.RESUMED] state, while all other fragments are
 * capped at [Lifecycle.State.STARTED]. If [.BEHAVIOR_SET_USER_VISIBLE_HINT] is
 * passed, all fragments are in the [Lifecycle.State.RESUMED] state and there will be
 * callbacks to [Fragment.setUserVisibleHint].
 *
 * @param fm fragment manager that will interact with this adapter
 * @param behavior determines if only current fragments are in a resumed state
 */
@JvmOverloads constructor(
    private val mFragmentManager: FragmentManager,
    @param:Behavior private val mBehavior: Int = BEHAVIOR_SET_USER_VISIBLE_HINT
) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private val mFragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        BEHAVIOR_SET_USER_VISIBLE_HINT,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    )
    private annotation class Behavior

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    override fun startUpdate(container: ViewGroup) {
        check(container.id != View.NO_ID) {
            ("ViewPager with adapter " + this +
                    " requires a view id")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size > position) {
            val f = mFragments[position]
            if (f != null) {
//                if (mCurTransaction == null)
//                    mCurTransaction = mFragmentManager.beginTransaction()
//                mCurTransaction!!.detach(f)
//                mCurTransaction!!.attach(f).commitNowAllowingStateLoss()
                return f
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val fragment = getItem(position)
        if (DEBUG) Log.v(
            TAG, "Adding item #$position: f=$fragment")
        if (mSavedState.size > position) {
            val fss = mSavedState[position]
            if (fss != null) {
                fragment.setInitialSavedState(fss)
            }
        }
        while (mFragments.size <= position) {
            mFragments.add(null)
        }
        fragment.setMenuVisibility(false)
        if (mBehavior == BEHAVIOR_SET_USER_VISIBLE_HINT) {
            fragment.userVisibleHint = false
        }

        mFragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment, position.toString())

        if (mBehavior == BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.STARTED)
        }

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

//        while (mSavedState.size <= position) {
//            mSavedState.add(null)
//        }
//        mSavedState.set(position, if (fragment.isAdded)
//            mFragmentManager.saveFragmentInstanceState(fragment)
//        else
//            null)
        mFragments.set(position, null)

        mCurTransaction!!.remove(fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                if (mBehavior == BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    if (mCurTransaction == null) {
                        mCurTransaction = mFragmentManager.beginTransaction()
                    }
                    mCurTransaction!!.setMaxLifecycle(mCurrentPrimaryItem!!, Lifecycle.State.STARTED)
                } else {
                    mCurrentPrimaryItem!!.userVisibleHint = false
                }
            }
            fragment.setMenuVisibility(true)
            if (mBehavior == BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                if (mCurTransaction == null) {
                    mCurTransaction = mFragmentManager.beginTransaction()
                }
                mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            } else {
                fragment.userVisibleHint = true
            }

            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitNowAllowingStateLoss()
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        var state: Bundle? = null
        if (mSavedState.size > 0) {
            state = Bundle()
            val fss = arrayOfNulls<Fragment.SavedState?>(mSavedState.size)
            mSavedState.toTypedArray<Fragment.SavedState?>()
            state.putParcelableArray("states", fss)
        }
        for (i in mFragments.indices) {
            val f = mFragments[i]
            if (f != null && f.isAdded) {
                if (state == null) {
                    state = Bundle()
                }
                val key = "f$i"
                mFragmentManager.putFragment(state, key, f)
            }
        }
        return state
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        Log.e("FRAGULA", "RESTORED_FRAGMENTS___: state $state ")
        if (state != null) {
            val bundle = state as Bundle?
            bundle!!.classLoader = loader
            val fss = bundle.getParcelableArray("states")
            mSavedState.clear()
            mFragments.clear()
            if (fss != null) {
                for (i in fss.indices) {
                    mSavedState.add(fss[i] as Fragment.SavedState)
                }
            }
            val keys = bundle.keySet()
            Log.e("FRAGULA", "RESTORED_FRAGMENTS___: keys $keys ")
            for (key in keys) {
                if (key.startsWith("f")) {
                    val index = Integer.parseInt(key.substring(1))
                    val f = mFragmentManager.getFragment(bundle, key)
                    Log.e("FRAGULA", "RESTORED_FRAGMENTS___: ROOT ${f?.javaClass?.simpleName} ")
                    if (f != null) {
                        while (mFragments.size <= index) {
                            mFragments.add(null)
                        }
                        f.setMenuVisibility(false)
                        mFragments[index] = f
                    } else {
                        Log.w(TAG, "Bad fragment at key $key")
                    }
                }
            }
            onRestoredFragments(mFragments)
        }
    }

    open fun onRestoredFragments(fragments: ArrayList<Fragment?>?) { }

    companion object {
        private val TAG = "FragmentStatePagerAdapt"
        private val DEBUG = false

        /**
         * Indicates that [Fragment.setUserVisibleHint] will be called when the current
         * fragment changes.
         *
         * @see .FragmentStatePagerAdapter
         */
        @Deprecated("This behavior relies on the deprecated\n" +
                "      {@link Fragment#setUserVisibleHint(boolean)} API. Use\n" +
                "      {@link #BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT} to switch to its replacement,\n" +
                "      {@link FragmentTransaction#setMaxLifecycle}.\n" +
                "      ")
        const val BEHAVIOR_SET_USER_VISIBLE_HINT = 0

        /**
         * Indicates that only the current fragment will be in the [Lifecycle.State.RESUMED]
         * state. All other Fragments are capped at [Lifecycle.State.STARTED].
         *
         * @see .FragmentStatePagerAdapter
         */
        const val BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1
    }
}
/**
 * Constructor for [androidx.fragment.app.FragmentStatePagerAdapter] that sets the fragment manager for the
 * adapter. This is the equivalent of calling
 * [.FragmentStatePagerAdapter] and passing in
 * [.BEHAVIOR_SET_USER_VISIBLE_HINT].
 *
 *
 * Fragments will have [Fragment.setUserVisibleHint] called whenever the
 * current Fragment changes.
 *
 * @param fm fragment manager that will interact with this adapter
 */