package com.fragula2.sample.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors

val Fragment.supportActionBar: ActionBar?
    get() = (activity as? AppCompatActivity)?.supportActionBar

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.getColorAttr(@AttrRes attrRes: Int): Int {
    return MaterialColors.getColor(this, attrRes, "The attribute is not set in the current theme")
}