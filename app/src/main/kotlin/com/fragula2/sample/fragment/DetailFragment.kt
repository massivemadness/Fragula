package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentDetailBinding
import com.fragula2.sample.utils.*
import java.util.*

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<DetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.picture.load(navArgs.chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(36f))
        }
        binding.profile.setOnClickListener {
            val direction = DetailFragmentDirections.actionToProfileFragment(navArgs.chat)
            navController.navigate(direction)
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
        supportActionBar?.title = navArgs.chat.name
    }

    private fun randomImage(min: Int, max: Int): Int {
        val upperBound = max - min + 1
        return min + Random().nextInt(upperBound)
    }
}