package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentProfileBinding
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val navArgs by navArgs<ProfileFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.picture.load(navArgs.chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(52f))
        }
        binding.name.text = navArgs.chat.name
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = navArgs.chat.name
    }
}