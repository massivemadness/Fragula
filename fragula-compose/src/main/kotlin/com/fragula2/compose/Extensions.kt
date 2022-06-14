package com.fragula2.compose

internal fun <T> List<T>.penultOrNull(): T? {
    return if (size > 1) get(size - 2) else null
}