/*
 * Copyright 2023 Fragula contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
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
        binding.root.applySystemWindowInsetsPadding(applyBottom = true)

        binding.picture.load(navArgs.chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(36f))
        }
        binding.profile.setOnClickListener {
            val direction = DetailFragmentDirections.actionToProfileFragment(navArgs.chat)
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }
        val images = resources.obtainTypedArray(R.array.stock_images)
        val stock = images.getResourceId(randomImage(0, images.length() - 1), -1)
        binding.stockImage.load(stock) {
            crossfade(true)
            transformations(RoundedCornersTransformation(16f))
        }
        images.recycle()
        binding.send.setOnClickListener {
            context?.showToast("Send")
        }

        view.doOnPreDraw {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
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