package com.fragula2.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fragula2.navigation.SwipeBackDestination

class NavBackStackAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val currentList = mutableListOf<NavBackStackEntry>()

    override fun createFragment(position: Int): Fragment {
        val context = fragment.requireContext()
        val entry = currentList[position]
        var className = (entry.destination as SwipeBackDestination).className
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

    fun push(entry: NavBackStackEntry) {
        currentList.add(entry)
        notifyItemInserted(currentList.size - 1)
    }

    fun pop() {
        if (currentList.size <= 1) return
        val index = currentList.size - 1
        currentList.removeAt(index)
        notifyItemRemoved(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(entries: List<NavBackStackEntry>) {
        currentList.addAll(entries)
        notifyDataSetChanged()
    }
}