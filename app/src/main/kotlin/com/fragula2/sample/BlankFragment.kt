package com.fragula2.sample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fragula2.sample.databinding.FragmentBlankBinding
import java.util.*

class BlankFragment : Fragment(R.layout.fragment_blank) {

    private var _binding: FragmentBlankBinding? = null
    private val binding: FragmentBlankBinding
        get() = _binding!!

    private val navController by lazy { findNavController() }
    private val intArg by lazy { arguments?.getInt(ARG_INT, 1) ?: 1 }
    private val stringArg by lazy { arguments?.getString(ARG_STRING) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlankBinding.bind(view)

        intArg.let {
            binding.intArg.text = it.toString()
        }
        stringArg?.let {
            binding.stringArg.text = it
        }

        binding.actionNavigate.setOnClickListener {
            navController.navigate(R.id.blankFragment, bundleOf(
                ARG_INT to intArg + 1,
                ARG_STRING to "Hello World!",
            ))
        }
        binding.actionPop.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_INT = "int_arg"
        private const val ARG_STRING = "string_arg"
    }
}