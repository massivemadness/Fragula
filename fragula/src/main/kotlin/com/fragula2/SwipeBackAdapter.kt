package com.fragula2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

internal class SwipeBackAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val currentList = mutableListOf<String>()

    override fun createFragment(position: Int): Fragment {
        val context = fragment.requireContext()
        var className = currentList[position]
        if (className[0] == '.') {
            className = context.packageName + className
        }
        return fragment.childFragmentManager.fragmentFactory
            .instantiate(context.classLoader, className)
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