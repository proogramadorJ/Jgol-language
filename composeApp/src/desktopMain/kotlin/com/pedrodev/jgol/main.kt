package com.pedrodev.jgol

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.pedrodev.jgol.navigation.SetupNavigation

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Jgol IDE",

    ) {
        //App()
        SetupNavigation()
    }
}