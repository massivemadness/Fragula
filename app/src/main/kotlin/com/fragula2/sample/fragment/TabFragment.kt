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
import com.fragula2.sample.R
import com.fragula2.sample.adapter.CardAdapter
import com.fragula2.sample.databinding.FragmentTabBinding
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding

class TabFragment : Fragment(R.layout.fragment_tab) {

    private val binding by viewBinding(FragmentTabBinding::bind)
    private val navArgs by navArgs<TabFragmentArgs>()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.label.text = navArgs.label

        binding.button.setOnClickListener {
            val direction = TabFragmentDirections.actionToTabFragment("Nested")
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }

        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.adapter = CardAdapter()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.title = navArgs.label
    }
}