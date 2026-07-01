package com.ganesh.composepref

import kotlinx.browser.localStorage

actual class KeyValueStorageFactory {
    actual fun create(name: String): KeyValueStorage {
        return WebKeyValueStorage(name)
    }
}

class WebKeyValueStorage(private val prefix: String) : KeyValueStorage {
    private fun fullKey(key: String): String = "$prefix.$key"

    override fun putString(key: String, value: String) {
        localStorage.setItem(fullKey(key), value)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return localStorage.getItem(fullKey(key)) ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        localStorage.setItem(fullKey(key), value.toString())
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return localStorage.getItem(fullKey(key))?.toIntOrNull() ?: defaultValue
    }

    override fun putLong(key: String, value: Long) {
        localStorage.setItem(fullKey(key), value.toString())
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return localStorage.getItem(fullKey(key))?.toLongOrNull() ?: defaultValue
    }

    override fun putFloat(key: String, value: Float) {
        localStorage.setItem(fullKey(key), value.toString())
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return localStorage.getItem(fullKey(key))?.toFloatOrNull() ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        localStorage.setItem(fullKey(key), value.toString())
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return localStorage.getItem(fullKey(key))?.toBooleanStrictOrNull() ?: defaultValue
    }

    override fun remove(key: String) {
        localStorage.removeItem(fullKey(key))
    }

    override fun clear() {
        val keysToRemove = mutableListOf<String>()
        val len = localStorage.length
        for (i in 0 until len) {
            val k = localStorage.key(i)
            if (k != null && k.startsWith("$prefix.")) {
                keysToRemove.add(k)
            }
        }
        keysToRemove.forEach { localStorage.removeItem(it) }
    }

    override fun contains(key: String): Boolean {
        return localStorage.getItem(fullKey(key)) != null
    }
}
