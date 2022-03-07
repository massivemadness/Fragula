package com.blacksquircle.fragula.common

@Deprecated("Use addFragment with BundleBuilder")
class Arg<K, V>(val key: K?, val value: V?) {

    override fun toString(): String {
        return String.format("Arg{%s %s}", key.toString(), value.toString())
    }

    companion object {
        fun <A, B> create(a: A?, b: B?): Arg<A?, B?> {
            return Arg(a, b)
        }
    }
}