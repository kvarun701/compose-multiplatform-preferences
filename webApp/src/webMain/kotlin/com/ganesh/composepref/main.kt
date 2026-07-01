package com.ganesh.composepref

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val storage = KeyValueStorageFactory().create("app_prefs")
    ComposeViewport {
        App(storage = storage)
    }
}