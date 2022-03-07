package com.blacksquircle.fragula.sample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blacksquircle.fragula.sample.databinding.FragmentBlankBinding
import java.util.*

class BlankFragment : Fragment(R.layout.fragment_blank) {

    private var _binding: FragmentBlankBinding? = null
    private val binding: FragmentBlankBinding
        get() = _binding!!

    private val navController by lazy { findNavController() }
    private val param1 by lazy { arguments?.getInt(ARG_PARAM_1) }
    private val param2 by lazy { arguments?.getInt(ARG_PARAM_2) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlankBinding.bind(view)

        param1?.let { binding.arg1.text = it.toString() }
        param2?.let { binding.arg2.text = it.toString() }

        binding.counter.text = (navController.backQueue.size - 1).toString()

        binding.actionNavigate.setOnClickListener {
            navController.navigate(R.id.blankFragment, bundleOf(
                ARG_PARAM_1 to Random().nextInt(),
                ARG_PARAM_2 to Random().nextInt(),
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
        private const val ARG_PARAM_1 = "param1"
        private const val ARG_PARAM_2 = "param2"
    }
}