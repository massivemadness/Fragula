package com.fragula2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SwipeBackAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val currentList = mutableListOf<String>()

    override fun createFragment(position: Int): Fragment {
        return fragment.childFragmentManager.fragmentFactory
            .instantiate(fragment.requireContext().classLoader, currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun push(className: String) {
        currentList.add(className)
        notifyItemInserted(currentList.size - 1)
    }

    fun pop() {
        val index = currentList.size - 1
        currentList.removeAt(index)
        notifyItemRemoved(index)
    }
}