package com.fragula2.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val simpleFlow = view.findViewById<MaterialButton>(R.id.simple_flow)
        simpleFlow.setOnClickListener {
            navController.navigate(R.id.simple_nav_graph)
        }

        /*val chatFlow = view.findViewById<MaterialButton>(R.id.chat_flow)
        chatFlow.setOnClickListener {
            // navController.navigate(R.id.chat_nav_graph)
        }

        val bottomNavFlow = view.findViewById<MaterialButton>(R.id.bottomnav_flow)
        bottomNavFlow.setOnClickListener {
            // navController.navigate(R.id.bottomnav_nav_graph)
        }*/
    }
}