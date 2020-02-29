package com.fragulo.navigator.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fragulo.navigator.adapter.base.FragmentStatePagerAdapter

class NavigatorAdapter(private val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {


    val fragments: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyDataSetChanged()
    }

    fun replaceFragment(position: Int, fragment: Fragment) {
        fragments[position] = fragment
        notifyDataSetChanged()
    }


    fun removeLastFragment() {
        fragmentManager.beginTransaction().remove(fragments.last()).commitNowAllowingStateLoss()
        fragments.removeAt(fragments.size - 1)
        notifyDataSetChanged()
    }

    fun getSizeListOfFragments(): Int {
        return fragments.size
    }

    fun getFragmentsCount(): Int {
        return fragments.size
    }

    override fun getItemPosition(`object`: Any): Int {
        val index = fragments.indexOf(`object`)
        return if (index == -1)
            POSITION_NONE
        else
            index
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override val count: Int
        get() = fragments.size


    override fun onRestoredFragments(fragments: ArrayList<Fragment?>?) {
        this.fragments.clear()
        fragments?.forEach {
            if (it != null) {
                this.fragments.add(it)
            }
        }
        notifyDataSetChanged()
    }
}

