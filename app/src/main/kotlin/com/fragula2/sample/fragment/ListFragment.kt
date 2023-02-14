package com.fragula2.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import com.fragula2.sample.adapter.ChatAdapter
import com.fragula2.sample.databinding.FragmentListBinding
import com.fragula2.sample.utils.applySystemWindowInsetsPadding
import com.fragula2.sample.utils.showToast
import com.fragula2.sample.utils.supportActionBar
import com.fragula2.sample.utils.viewBinding
import java.util.*

class ListFragment : Fragment(R.layout.fragment_list) {

    private val binding by viewBinding(FragmentListBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.applySystemWindowInsetsPadding(applyBottom = true)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL),
        )
        binding.recyclerView.adapter = ChatAdapter { chat ->
            val direction = ListFragmentDirections.actionToDetailFragment(chat)
            navController.currentDestination?.getAction(direction.actionId)
                ?.run { navController.navigate(direction) }
        }.also { adapter ->
            val names = resources.getStringArray(R.array.people_names)
            val images = resources.obtainTypedArray(R.array.people_images)
            adapter.submitList(
                names.mapIndexed { index, name ->
                    Chat(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        image = images.getResourceId(index, -1),
                        lastMessage = R.string.lorem_ipsum,
                    )
                },
            )
            images.recycle()
        }
        binding.fab.setOnClickListener {
            context?.showToast("New chat")
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setTitle(R.string.app_name)
    }
}