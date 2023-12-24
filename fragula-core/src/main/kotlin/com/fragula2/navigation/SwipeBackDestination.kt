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

package com.fragula2.navigation

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.Navigator
import com.fragula2.R

@NavDestination.ClassType(Fragment::class)
class SwipeBackDestination(
    swipeBackNavigator: Navigator<out SwipeBackDestination>,
) : NavDestination(swipeBackNavigator) {

    private var _className: String? = null
    val className: String
        get() {
            checkNotNull(_className) { "Fragment class was not set" }
            return _className as String
        }

    override fun onInflate(context: Context, attrs: AttributeSet) {
        super.onInflate(context, attrs)
        context.resources.obtainAttributes(attrs, R.styleable.SwipeBackNavigator).use { array ->
            val className = array.getString(R.styleable.SwipeBackNavigator_android_name)
            if (className != null) {
                _className = className
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SwipeBackDestination) return false
        return super.equals(other) && className == other.className
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + className.hashCode()
        return result
    }
}