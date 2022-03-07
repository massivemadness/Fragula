package com.fragula.common

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KProperty

class BundleBuilder(val bundle: Bundle = Bundle()) {
    /**
     * If this function is called, the type cannot be put in a Bundle.
     */
    infix fun <T> String.to(v: Any) {
        throw IllegalArgumentException()
    }

    infix fun String.to(v: Parcelable?) {
        bundle.putParcelable(this, v)
    }

    infix fun String.to(v: Boolean) {
        bundle.putBoolean(this, v)
    }

    infix fun String.to(v: Byte) {
        bundle.putByte(this, v)
    }

    infix fun String.to(v: Char) {
        bundle.putChar(this, v)
    }

    infix fun String.to(v: Short) {
        bundle.putShort(this, v)
    }

    infix fun String.to(v: Int) {
        bundle.putInt(this, v)
    }

    infix fun String.to(v: Long) {
        bundle.putLong(this, v)
    }

    infix fun String.to(v: Float) {
        bundle.putFloat(this, v)
    }

    infix fun String.to(v: Double) {
        bundle.putDouble(this, v)
    }

    infix fun String.to(v: String) {
        bundle.putString(this, v)
    }

    infix fun String.to(v: CharSequence) {
        bundle.putCharSequence(this, v)
    }

    infix fun String.toIntList(v: ArrayList<Int>) {
        bundle.putIntegerArrayList(this, v)
    }

    infix fun String.toStringList(v: ArrayList<String>) {
        bundle.putStringArrayList(this, v)
    }

    infix fun String.toCharSequenceList(v: ArrayList<CharSequence>) {
        bundle.putCharSequenceArrayList(this, v)
    }

    infix fun String.to(v: Serializable?) {
        bundle.putSerializable(this, v)
    }

    infix fun String.to(v: BooleanArray) {
        bundle.putBooleanArray(this, v)
    }

    infix fun String.to(v: ByteArray) {
        bundle.putByteArray(this, v)
    }

    infix fun String.to(v: ShortArray) {
        bundle.putShortArray(this, v)
    }

    infix fun String.to(v: CharArray) {
        bundle.putCharArray(this, v)
    }

    infix fun String.to(v: IntArray) {
        bundle.putIntArray(this, v)
    }

    infix fun String.to(v: LongArray) {
        bundle.putLongArray(this, v)
    }

    infix fun String.to(v: FloatArray) {
        bundle.putFloatArray(this, v)
    }

    infix fun String.to(v: DoubleArray) {
        bundle.putDoubleArray(this, v)
    }

    infix fun String.to(v: Array<String>) {
        bundle.putStringArray(this, v)
    }

    @Suppress("UNCHECKED_CAST")
    inline infix fun <reified T> String.to(v: ArrayList<T>) {
        if (T::class.java.isAssignableFrom(Int::class.java)) {
            bundle.putIntegerArrayList(this, v as ArrayList<Int>)
        } else if (T::class.java.isAssignableFrom(String::class.java)) {
            bundle.putStringArrayList(this, v as ArrayList<String>)
        } else if (T::class.java.isAssignableFrom(CharSequence::class.java)) {
            bundle.putCharSequenceArrayList(this, v as ArrayList<CharSequence>)
        } else {
            bundle.putSerializable(this, v)
        }
    }

    /**
     * Если вызвана эта функция, то тип нельзя положить в Bundle.
     */
    infix fun <T> KProperty<*>.to(v: Any) {
        throw IllegalArgumentException()
    }

    infix fun KProperty<*>.to(v: Parcelable?) {
        bundle.putParcelable(name, v)
    }

    infix fun KProperty<*>.to(v: Boolean) {
        bundle.putBoolean(name, v)
    }

    infix fun KProperty<*>.to(v: Byte) {
        bundle.putByte(name, v)
    }

    infix fun KProperty<*>.to(v: Char) {
        bundle.putChar(name, v)
    }

    infix fun KProperty<*>.to(v: Short) {
        bundle.putShort(name, v)
    }

    infix fun KProperty<*>.to(v: Int) {
        bundle.putInt(name, v)
    }

    infix fun KProperty<*>.to(v: Long) {
        bundle.putLong(name, v)
    }

    infix fun KProperty<*>.to(v: Float) {
        bundle.putFloat(name, v)
    }

    infix fun KProperty<*>.to(v: Double) {
        bundle.putDouble(name, v)
    }

    infix fun KProperty<*>.to(v: String) {
        bundle.putString(name, v)
    }

    infix fun KProperty<*>.to(v: CharSequence) {
        bundle.putCharSequence(name, v)
    }

    infix fun KProperty<*>.toIntList(v: ArrayList<Int>) {
        bundle.putIntegerArrayList(name, v)
    }

    infix fun KProperty<*>.toStringList(v: ArrayList<String>) {
        bundle.putStringArrayList(name, v)
    }

    infix fun KProperty<*>.toCharSequenceList(v: ArrayList<CharSequence>) {
        bundle.putCharSequenceArrayList(name, v)
    }

    infix fun KProperty<*>.to(v: Serializable?) {
        bundle.putSerializable(name, v)
    }

    infix fun KProperty<*>.to(v: BooleanArray) {
        bundle.putBooleanArray(name, v)
    }

    infix fun KProperty<*>.to(v: ByteArray) {
        bundle.putByteArray(name, v)
    }

    infix fun KProperty<*>.to(v: ShortArray) {
        bundle.putShortArray(name, v)
    }

    infix fun KProperty<*>.to(v: CharArray) {
        bundle.putCharArray(name, v)
    }

    infix fun KProperty<*>.to(v: IntArray) {
        bundle.putIntArray(name, v)
    }

    infix fun KProperty<*>.to(v: LongArray) {
        bundle.putLongArray(name, v)
    }

    infix fun KProperty<*>.to(v: FloatArray) {
        bundle.putFloatArray(name, v)
    }

    infix fun KProperty<*>.to(v: DoubleArray) {
        bundle.putDoubleArray(name, v)
    }

    infix fun KProperty<*>.to(v: Array<String>) {
        bundle.putStringArray(name, v)
    }

    @Suppress("UNCHECKED_CAST")
    inline infix fun <reified T> KProperty<*>.to(v: ArrayList<T>) {
        if (T::class.java.isAssignableFrom(Int::class.java)) {
            bundle.putIntegerArrayList(name, v as ArrayList<Int>)
        } else if (T::class.java.isAssignableFrom(String::class.java)) {
            bundle.putStringArrayList(name, v as ArrayList<String>)
        } else if (T::class.java.isAssignableFrom(CharSequence::class.java)) {
            bundle.putCharSequenceArrayList(name, v as ArrayList<CharSequence>)
        } else {
            bundle.putSerializable(name, v)
        }
    }
}