package com.ganesh.composepref

import platform.Foundation.NSUserDefaults

actual class KeyValueStorageFactory {
    actual fun create(name: String): KeyValueStorage {
        val defaults = NSUserDefaults(suiteName = name) ?: NSUserDefaults.standardUserDefaults
        return IosKeyValueStorage(defaults)
    }
}

class IosKeyValueStorage(private val defaults: NSUserDefaults) : KeyValueStorage {
    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return defaults.stringForKey(key) ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), forKey = key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key).toInt()
        } else {
            defaultValue
        }
    }

    override fun putLong(key: String, value: Long) {
        defaults.setInteger(value, forKey = key)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key)
        } else {
            defaultValue
        }
    }

    override fun putFloat(key: String, value: Float) {
        defaults.setFloat(value, forKey = key)
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return if (defaults.objectForKey(key) != null) {
            defaults.floatForKey(key)
        } else {
            defaultValue
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else {
            defaultValue
        }
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    override fun clear() {
        defaults.dictionaryRepresentation().keys.forEach { key ->
            if (key is String) {
                defaults.removeObjectForKey(key)
            }
        }
    }

    override fun contains(key: String): Boolean {
        return defaults.objectForKey(key) != null
    }
}
