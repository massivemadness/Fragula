package com.fragula2.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SwipeBackAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val currentList = mutableListOf<FragulaEntry>()

    override fun createFragment(position: Int): Fragment {
        val context = fragment.requireContext()
        val entry = currentList[position]
        var className = entry.className
        if (className[0] == '.') {
            className = context.packageName + className
        }
        return fragment.childFragmentManager.fragmentFactory
            .instantiate(context.classLoader, className).apply {
                arguments = entry.arguments
            }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun push(entry: FragulaEntry) {
        currentList.add(entry)
        notifyItemInserted(currentList.size - 1)
    }

    fun pop() {
        if (currentList.size > 1) {
            val index = currentList.size - 1
            currentList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(entries: List<FragulaEntry>) {
        currentList.addAll(entries)
        notifyDataSetChanged()
    }
}