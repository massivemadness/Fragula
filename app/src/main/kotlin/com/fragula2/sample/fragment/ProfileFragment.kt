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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.R
import com.fragula2.sample.databinding.FragmentProfileBinding
import com.fragula2.sample.utils.applySystemWindowInsetsPadding
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val navArgs by navArgs<ProfileFragmentArgs>()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.applySystemWindowInsetsPadding(applyBottom = true)

        binding.picture.load(navArgs.chat.image) {
            crossfade(true)
            transformations(RoundedCornersTransformation(52f))
        }
        binding.name.text = navArgs.chat.name

        binding.instagram.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Instagram")
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }
        binding.facebook.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Facebook")
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }
        binding.telegram.setOnClickListener {
            val direction = ProfileFragmentDirections.actionToTabFragment("Telegram")
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = navArgs.chat.name
    }
}