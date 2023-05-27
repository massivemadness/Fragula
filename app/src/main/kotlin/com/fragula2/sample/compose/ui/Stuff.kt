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

package com.fragula2.sample.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import java.util.*

@Composable
fun getChats(): List<Chat> {
    val context = LocalContext.current
    val names = context.resources.getStringArray(R.array.people_names)
    val images = context.resources.obtainTypedArray(R.array.people_images)
    val chats = names.mapIndexed { index, name ->
        Chat(
            id = index.toString(),
            name = name,
            image = images.getResourceId(index, -1),
            lastMessage = R.string.lorem_ipsum,
        )
    }
    images.recycle()
    return chats
}

@Composable
fun randomImage(): Painter {
    val images = LocalContext.current.resources.obtainTypedArray(R.array.stock_images)
    val min = 0
    val max = images.length() - 1
    val upperBound = max - min + 1
    val result = min + Random().nextInt(upperBound)
    val stock = images.getResourceId(result, -1)
    images.recycle()
    return painterResource(stock)
}