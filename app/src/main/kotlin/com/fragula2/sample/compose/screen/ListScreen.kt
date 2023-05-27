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

package com.fragula2.sample.compose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import com.fragula2.sample.compose.ui.FragulaTheme
import com.fragula2.sample.compose.ui.getChats

@Composable
fun ListScreen(navController: NavController) {
    val chats = getChats()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chats, key = Chat::id) {
            ChatItem(it) {
                navController.navigate("details/${it.id}")
            }
            Divider()
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit = {}) {
    val chatState by remember { mutableStateOf(chat) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() },
    ) {
        Image(
            painter = painterResource(chat.image),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(50.dp)
                .align(alignment = Alignment.CenterVertically)
                .clip(CircleShape),
        )
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 16.dp),
        ) {
            Text(
                text = chatState.name,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
            )
            Text(
                text = stringResource(chatState.lastMessage),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.onBackground,
                fontSize = 16.sp,
            )
        }
    }
}

@Preview
@Composable
fun ChatItemPreviewLight() {
    val chatMock = Chat(
        id = "1",
        name = "Antonio Maretti",
        image = R.drawable.photo_female_1,
        lastMessage = R.string.lorem_ipsum,
    )
    FragulaTheme(darkTheme = false) {
        ChatItem(
            chat = chatMock,
            onClick = {},
        )
    }
}

@Preview
@Composable
fun ChatItemPreviewDark() {
    val chatMock = Chat(
        id = "1",
        name = "Antonio Maretti",
        image = R.drawable.photo_female_1,
        lastMessage = R.string.lorem_ipsum,
    )
    FragulaTheme(darkTheme = true) {
        ChatItem(
            chat = chatMock,
            onClick = {},
        )
    }
}