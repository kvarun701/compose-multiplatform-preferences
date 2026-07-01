package com.ganesh.composepref

expect class KeyValueStorageFactory {
    fun create(name: String = "app_preferences"): KeyValueStorage
}
