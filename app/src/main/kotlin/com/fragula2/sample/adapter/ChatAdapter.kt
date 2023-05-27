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

package com.fragula2.sample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fragula2.sample.databinding.ItemChatBinding

class ChatAdapter(
    private val onClick: (Chat) -> Unit,
) : ListAdapter<Chat, ChatAdapter.ChatViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(
        private val binding: ItemChatBinding,
        private val onClick: (Chat) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onClick: (Chat) -> Unit): ChatViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemChatBinding.inflate(inflater, parent, false)
                return ChatViewHolder(binding, onClick)
            }
        }

        private lateinit var chat: Chat

        init {
            itemView.setOnClickListener {
                onClick(chat)
            }
        }

        fun bind(chat: Chat) {
            this.chat = chat
            binding.name.text = chat.name
            binding.description.setText(chat.lastMessage)
            binding.image.load(chat.image) {
                crossfade(true)
                transformations(RoundedCornersTransformation(72f))
            }
        }
    }
}