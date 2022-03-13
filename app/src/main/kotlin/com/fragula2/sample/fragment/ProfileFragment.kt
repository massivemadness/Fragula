package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import com.fragula2.sample.databinding.FragmentProfileBinding
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val chat by lazy { requireArguments().getParcelable<Chat>("CHAT")!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.picture.load(chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(52f))
        }
        binding.name.text = chat.name
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = chat.name
    }
}