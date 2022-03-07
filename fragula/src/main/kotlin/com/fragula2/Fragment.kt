package com.fragula2

import androidx.fragment.app.Fragment
import androidx.navigation.NavController

fun Fragment.findNavController(): NavController =
    FragulaNavHostFragment.findNavController(this)