package com.pedrodev.jgol

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.pedrodev.jgol.ide.Session
import com.pedrodev.jgol.navigation.SetupNavigation
import java.awt.Toolkit

fun main() = application {

    Session.initSession()
    val screenSize = Toolkit.getDefaultToolkit().screenSize

    // println("width ${screenSize.width} px")
    // println("height ${screenSize.height} px")

    val screen = object {
        val width = ((screenSize.width * 0.80) / 1.5).toInt()
        val height = ((screenSize.height * 1) / 1.5).toInt()
    }

    //println("width ${screen.width} dp")
    // println("heiht ${screen.height} dp")
    Window(

        onCloseRequest = ::exitApplication,
        title = "Jgol IDE",
        state = rememberWindowState(width = screen.width.dp, height = screen.height.dp)

    ) {
        SetupNavigation()

    }
}