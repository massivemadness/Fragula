package com.fragula2.sample.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import com.fragula2.sample.databinding.FragmentDetailBinding
import com.fragula2.sample.utils.showToast
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding
import java.util.*

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val navController by lazy { findNavController() }
    private val chat by lazy { requireArguments().getParcelable<Chat>("CHAT")!! }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.picture.load(chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(36f))
        }
        binding.profile.setOnClickListener {
            navController.navigate(R.id.profileFragment, bundleOf("CHAT" to chat))
        }
        val images = resources.obtainTypedArray(R.array.stock_images)
        val stock = images.getResourceId(randomImage(1, images.length()), -1)
        binding.stockImage.load(stock) {
            crossfade(true)
            transformations(RoundedCornersTransformation(16f))
        }
        images.recycle()
        binding.send.setOnClickListener {
            context?.showToast("Send")
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = chat.name
    }

    private fun randomImage(min: Int, max: Int): Int {
        val upperBound = max - min + 1
        return min + Random().nextInt(upperBound)
    }
}