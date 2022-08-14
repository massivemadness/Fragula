package com.fragula2.sample.compose.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fragula2.sample.R
import com.fragula2.sample.adapter.Chat
import com.fragula2.sample.compose.ui.FragulaTheme
import com.fragula2.sample.compose.ui.getChats
import com.fragula2.sample.compose.ui.randomImage

@Composable
fun DetailsScreen(navController: NavController, chatId: String) {
    val chat = getChats().find { it.id == chatId } ?: error("Chat not found")
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        ProfileButton(chat) {
            navController.navigate("profile/$chatId")
        }
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Bottom,
        ) {
            MessageBox(
                fromMe = true,
                picture = null,
                time = "12:31",
                modifier = Modifier.align(Alignment.End)
            )
            MessageBox(
                fromMe = false,
                picture = randomImage(),
                time = "12:38",
                modifier = Modifier.align(Alignment.Start)
            )
            MessageBox(
                fromMe = true,
                picture = null,
                time = "12:45",
                modifier = Modifier.align(Alignment.End)
            )
            MessageBox(
                fromMe = false,
                picture = null,
                time = "12:47",
                modifier = Modifier.align(Alignment.Start)
            )
        }
        SendBox {
            // ignore input
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ProfileButton(chat: Chat, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(28.dp)
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = painterResource(chat.image),
                contentDescription = null,
                modifier = Modifier.padding(2.dp)
                    .clip(RoundedCornerShape(36f)),
            )
            Text(
                text = stringResource(R.string.open_profile),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(horizontal = 6.dp)
                    .align(Alignment.CenterVertically),
                fontSize = TextUnit(
                    value = 16f,
                    type = TextUnitType.Sp,
                ),
            )
        }
    }
}

@Preview
@Composable
fun ProfileButtonPreview() {
    FragulaTheme {
        ProfileButton(
            chat = Chat(
                id = "1",
                name = "Person Name",
                image = R.drawable.photo_female_1,
                lastMessage = R.string.lorem_ipsum
            ),
            onClick = {}
        )
    }
}

@Composable
fun SendBox(onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .height(48.dp)
    ) {
        Row {
            val message = stringResource(R.string.message)
            var text by rememberSaveable {
                mutableStateOf(message)
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                /*placeholder = {
                    Text(text = stringResource(R.string.message))
                },*/
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.onPrimary),
                singleLine = true,
                modifier = Modifier.weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(horizontal = 12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false)
                    ) { onClick(text) },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
        }
    }
}

@Preview
@Composable
fun SendBoxPreview() {
    FragulaTheme {
        SendBox {}
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MessageBox(
    fromMe: Boolean,
    picture: Painter?,
    time: String,
    modifier: Modifier = Modifier
) {
    val image = remember { picture }
    Card(
        modifier = modifier.requiredWidth(280.dp)
            .padding(all = 6.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
        backgroundColor = if (fromMe) {
            MaterialTheme.colors.secondaryVariant
        } else {
            MaterialTheme.colors.surface
        },
    ) {
        Box {
            Text(
                text = stringResource(R.string.lorem_ipsum),
                modifier = Modifier.padding(all = 8.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = TextUnit(
                    value = 16f,
                    type = TextUnitType.Sp,
                ),
            )
            if (image != null) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.padding(
                        top = 56.dp, // TODO use column instead
                        start = 8.dp,
                        bottom = 8.dp
                    ).clip(RoundedCornerShape(16f)),
                )
            }
            Text(
                text = time,
                modifier = Modifier.padding(all = 4.dp)
                    .padding(end = 4.dp)
                    .align(Alignment.BottomEnd),
                color = MaterialTheme.colors.onBackground,
                fontSize = TextUnit(
                    value = 12f,
                    type = TextUnitType.Sp,
                ),
            )
        }
    }
}