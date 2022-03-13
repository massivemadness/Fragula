package com.fragula2.sample.adapter

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: String,
    val name: String,
    @DrawableRes val image: Int,
    @StringRes val lastMessage: Int,
) : Parcelable