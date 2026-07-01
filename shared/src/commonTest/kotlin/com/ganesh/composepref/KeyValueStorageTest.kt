package com.ganesh.composepref

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNull

class KeyValueStorageTest {

    private val storage = InMemoryKeyValueStorage()

    @Test
    fun testBasicStorageOperations() {
        storage.clear()

        assertFalse(storage.contains("test_string"))
        assertNull(storage.getString("test_string"))

        storage.putString("test_string", "hello")
        assertTrue(storage.contains("test_string"))
        assertEquals("hello", storage.getString("test_string"))

        storage.putInt("test_int", 42)
        assertEquals(42, storage.getInt("test_int"))

        storage.putLong("test_long", 1234567890L)
        assertEquals(1234567890L, storage.getLong("test_long"))

        storage.putFloat("test_float", 3.14f)
        assertEquals(3.14f, storage.getFloat("test_float"))

        storage.putBoolean("test_bool", true)
        assertTrue(storage.getBoolean("test_bool"))

        storage.remove("test_string")
        assertFalse(storage.contains("test_string"))
        assertNull(storage.getString("test_string"))

        storage.clear()
        assertFalse(storage.contains("test_int"))
        assertFalse(storage.contains("test_bool"))
        assertFalse(storage.contains("test_long"))
        assertFalse(storage.contains("test_float"))
    }

    @Test
    fun testDelegates() {
        storage.clear()

        class Settings(kv: KeyValueStorage) {
            var theme by kv.string("theme", "light")
            var volume by kv.int("volume", 50)
            var notifications by kv.boolean("notifications", true)
        }

        val settings = Settings(storage)

        assertEquals("light", settings.theme)
        assertEquals(50, settings.volume)
        assertTrue(settings.notifications)

        settings.theme = "dark"
        settings.volume = 90
        settings.notifications = false

        assertEquals("dark", storage.getString("theme"))
        assertEquals(90, storage.getInt("volume"))
        assertFalse(storage.getBoolean("notifications"))

        assertEquals("dark", settings.theme)
        assertEquals(90, settings.volume)
        assertFalse(settings.notifications)
    }
}
