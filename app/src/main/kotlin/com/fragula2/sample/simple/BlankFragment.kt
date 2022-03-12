package com.fragula2.sample.simple

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentBlankBinding
import com.fragula2.sample.utils.viewBinding

class BlankFragment : Fragment(R.layout.fragment_blank) {

    private val binding by viewBinding(FragmentBlankBinding::bind)
    private val navController by lazy { findNavController() }
    private val intArg by lazy { arguments?.getInt(ARG_INT, 1) ?: 1 }
    private val stringArg by lazy { arguments?.getString(ARG_STRING) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionNavigate.setOnClickListener {
            val bundle = bundleOf(
                ARG_INT to intArg + 1,
                ARG_STRING to "Hello World!",
            )
            navController.navigate(R.id.blankFragment, bundle)
        }
        binding.actionPop.setOnClickListener {
            navController.popBackStack()
        }

        intArg.let { binding.intArg.text = it.toString() }
        stringArg?.let { binding.stringArg.text = it }
    }

    companion object {
        private const val ARG_INT = "int_arg"
        private const val ARG_STRING = "string_arg"
    }
}