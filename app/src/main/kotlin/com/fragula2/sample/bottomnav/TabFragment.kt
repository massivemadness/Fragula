package com.fragula2.sample.bottomnav

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentTabBinding

class TabFragment : Fragment(R.layout.fragment_tab) {

    private var _binding: FragmentTabBinding? = null
    private val binding: FragmentTabBinding
        get() = _binding!!

    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTabBinding.bind(view)
        binding.actionNavigate.setOnClickListener {
            navController.navigate(R.id.blankFragment)
        }
        binding.actionPop.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}