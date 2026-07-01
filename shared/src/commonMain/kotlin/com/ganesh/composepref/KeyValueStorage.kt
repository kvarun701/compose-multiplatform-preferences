package com.ganesh.composepref

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface KeyValueStorage {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Long

    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0.0f): Float

    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun remove(key: String)
    fun clear()
    fun contains(key: String): Boolean
}

// Property Delegates for elegant preference fields

fun KeyValueStorage.string(key: String, defaultValue: String? = null): ReadWriteProperty<Any?, String?> =
    object : ReadWriteProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? = getString(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value != null) putString(key, value) else remove(key)
        }
    }

fun KeyValueStorage.int(key: String, defaultValue: Int = 0): ReadWriteProperty<Any?, Int> =
    object : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = getInt(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) = putInt(key, value)
    }

fun KeyValueStorage.long(key: String, defaultValue: Long = 0L): ReadWriteProperty<Any?, Long> =
    object : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long = getLong(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) = putLong(key, value)
    }

fun KeyValueStorage.float(key: String, defaultValue: Float = 0.0f): ReadWriteProperty<Any?, Float> =
    object : ReadWriteProperty<Any?, Float> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Float = getFloat(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) = putFloat(key, value)
    }

fun KeyValueStorage.boolean(key: String, defaultValue: Boolean = false): ReadWriteProperty<Any?, Boolean> =
    object : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = getBoolean(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = putBoolean(key, value)
    }

// In-Memory Implementation for testing and UI previews

class InMemoryKeyValueStorage : KeyValueStorage {
    private val map = mutableMapOf<String, Any>()

    override fun putString(key: String, value: String) { map[key] = value }
    override fun getString(key: String, defaultValue: String?): String? = map[key] as? String ?: defaultValue

    override fun putInt(key: String, value: Int) { map[key] = value }
    override fun getInt(key: String, defaultValue: Int): Int = map[key] as? Int ?: defaultValue

    override fun putLong(key: String, value: Long) { map[key] = value }
    override fun getLong(key: String, defaultValue: Long): Long = map[key] as? Long ?: defaultValue

    override fun putFloat(key: String, value: Float) { map[key] = value }
    override fun getFloat(key: String, defaultValue: Float): Float = map[key] as? Float ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) { map[key] = value }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = map[key] as? Boolean ?: defaultValue

    override fun remove(key: String) { map.remove(key) }
    override fun clear() { map.clear() }
    override fun contains(key: String): Boolean = map.containsKey(key)
}
