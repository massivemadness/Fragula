package com.blacksquircle.fragula.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blacksquircle.fragula.extensions.addFragment
import com.blacksquircle.fragula.extensions.parentNavigator
import com.blacksquircle.fragula.sample.databinding.FragmentBlankBinding

class BlankFragment : Fragment(R.layout.fragment_blank) {

    private var _binding: FragmentBlankBinding? = null
    private val binding: FragmentBlankBinding
        get() = _binding!!

    private val param1 by lazy { arguments?.getString(ARG_PARAM_1) }
    private val param2 by lazy { arguments?.getInt(ARG_PARAM_2) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlankBinding.bind(view)

        param1?.let { binding.arg1.text = it }
        param2?.let { binding.arg2.text = it.toString() }

        binding.actionNavigate.setOnClickListener {
            addFragment<BlankFragment>()
        }
        binding.actionPop.setOnClickListener {
            addFragment<BlankFragment> {
                ARG_PARAM_1 to "Add fragment arg"
                ARG_PARAM_2 to 12345
            }
        }

        view.post {
            binding.counter.text = parentNavigator.fragmentCount.toString()
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