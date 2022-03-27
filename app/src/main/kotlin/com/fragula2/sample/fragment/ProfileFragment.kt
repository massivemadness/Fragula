package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.picture.load(navArgs.chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(52f))
        }
        binding.name.text = navArgs.chat.name

        binding.instagram.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Instagram")
            navController.navigate(direction)
        }
        binding.facebook.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Facebook")
            navController.navigate(direction)
        }
        binding.telegram.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Telegram")
            navController.navigate(direction)
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = navArgs.chat.name
    }
}