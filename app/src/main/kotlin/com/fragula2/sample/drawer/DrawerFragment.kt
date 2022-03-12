package com.fragula2.sample.drawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentDrawerBinding
import com.fragula2.sample.utils.viewBinding

class DrawerFragment : Fragment(R.layout.fragment_drawer) {

    private val binding by viewBinding(FragmentDrawerBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionNavigate.setOnClickListener {
            navController.navigate(R.id.drawerFragment)
        }
        binding.actionPop.setOnClickListener {
            navController.popBackStack()
        }
    }
}