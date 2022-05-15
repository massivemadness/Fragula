package com.fragula2.navigation

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.Navigator
import com.fragula2.R

@NavDestination.ClassType(Fragment::class)
class SwipeBackDestination constructor(
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
        if (other == null || other !is SwipeBackDestination) return false
        return super.equals(other) && className == other.className
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + className.hashCode()
        return result
    }
}