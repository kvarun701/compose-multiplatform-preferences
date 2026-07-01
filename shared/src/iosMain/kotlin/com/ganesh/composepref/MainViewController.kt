package com.ganesh.composepref

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val storage = KeyValueStorageFactory().create("app_prefs")
    App(storage = storage)
}