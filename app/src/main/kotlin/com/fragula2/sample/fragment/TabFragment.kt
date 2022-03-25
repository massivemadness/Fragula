package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentTabBinding
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding

class TabFragment : Fragment(R.layout.fragment_tab) {

    private val binding by viewBinding(FragmentTabBinding::bind)
    private val navArgs by navArgs<TabFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.label.text = navArgs.label
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = navArgs.label
    }
}