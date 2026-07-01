package com.ganesh.composepref

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    val storage = KeyValueStorageFactory().create("app_prefs")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Composepref",
        ) {
            App(storage = storage)
        }
    }
}