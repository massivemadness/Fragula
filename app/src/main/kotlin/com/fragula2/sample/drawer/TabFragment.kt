package com.fragula2.sample.drawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentTabBinding
import com.fragula2.sample.utils.viewBinding

class TabFragment : Fragment(R.layout.fragment_tab) {

    private val binding by viewBinding(FragmentTabBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.label.text = arguments?.getString("LABEL")
    }
}