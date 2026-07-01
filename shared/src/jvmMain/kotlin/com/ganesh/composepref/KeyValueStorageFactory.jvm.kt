package com.ganesh.composepref

import java.util.prefs.Preferences

actual class KeyValueStorageFactory {
    actual fun create(name: String): KeyValueStorage {
        val cleanName = name.replace("[^a-zA-Z0-9_]".toRegex(), "_")
        val prefs = Preferences.userRoot().node("composepref/$cleanName")
        return JvmKeyValueStorage(prefs)
    }
}

class JvmKeyValueStorage(private val prefs: Preferences) : KeyValueStorage {
    override fun putString(key: String, value: String) {
        prefs.put(key, value)
        try {
            prefs.flush()
        } catch (e: Exception) {
            // Ignore flushing error in dry run or read-only environments
        }
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return prefs.get(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
        try {
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        prefs.putLong(key, value)
        try {
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun putFloat(key: String, value: Float) {
        prefs.putFloat(key, value)
        try {
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
        try {
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun remove(key: String) {
        prefs.remove(key)
        try {
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun clear() {
        try {
            prefs.clear()
            prefs.flush()
        } catch (e: Exception) {
        }
    }

    override fun contains(key: String): Boolean {
        return try {
            prefs.keys().contains(key)
        } catch (e: Exception) {
            false
        }
    }
}
