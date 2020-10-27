package com.fragula.extensions

import android.content.res.Resources
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import com.fragula.navigator.BuildConfig

internal fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

internal val <T : Any> T.simpleName
    get() = this::class.simpleName


internal val Int.dp: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

internal val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

internal val Int.px: Int
    get() = ((this / Resources.getSystem().displayMetrics.density) + 0.5f).toInt()

internal val Float.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density) + 0.5f

internal fun Int.isOdd() = this and 0x1 == 1

internal fun Int.isEven() = !this.isOdd()

internal inline fun ifNotNull(vararg values: Any?, crossinline block: () -> Unit): Unit? {
    values.forEach {
        if (it == null) return null
    }
    return block.invoke()
}

internal inline fun tryCatch(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Log.e("TRY_CATCH ___________:", "${e.simpleName}: ${e.message}")
        e.stackTrace.forEach {
            Log.e("TRY_CATCH: ", "$it")
        }
    }
}

internal inline fun <T> tryCatch(blockTry: () -> T, blockCatch: () -> T): T {
    return try {
        blockTry.invoke()
    } catch (e: Exception) {
        Log.e("TRY_CATCH ___________:", "${e.simpleName}: ${e.message}")
        e.stackTrace.forEach {
            Log.e("TRY_CATCH: ", "$it")
        }
        blockCatch()
    }
}

internal fun log(text: Any? = null) {
    if (BuildConfig.DEBUG) {
        tryCatch {
            val stackTrace = Thread.currentThread().stackTrace
            var path = stackTrace[3].toString()
            path = path.replace("${BuildConfig.LIBRARY_PACKAGE_NAME}.", "")
            Log.i("FRAGULA", "${path}: ${text.toString()}")
        }
    }
}
