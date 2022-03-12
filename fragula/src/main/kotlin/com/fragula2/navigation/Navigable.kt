package com.fragula2.navigation

import com.fragula2.adapter.FragulaEntry

interface Navigable {
    fun navigate(entry: FragulaEntry)
    fun popBackStack()
}