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

package com.fragula2.sample.utils

import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

fun Window.decorFitsSystemWindows(whether: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(this, whether)
}

fun View.applySystemWindowInsetsPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarType = WindowInsetsCompat.Type.statusBars()
        val navigationBarType = WindowInsetsCompat.Type.navigationBars()
        val systemWindowInsets = insets.getInsets(statusBarType or navigationBarType)

        val leftInset = if (applyLeft) systemWindowInsets.left else view.paddingLeft
        val topInset = if (applyTop) systemWindowInsets.top else view.paddingTop
        val rightInset = if (applyRight) systemWindowInsets.right else view.paddingRight
        val bottomInset = if (applyBottom) systemWindowInsets.bottom else view.paddingBottom

        view.updatePadding(
            left = leftInset,
            top = topInset,
            right = rightInset,
            bottom = bottomInset,
        )

        insets
    }
}