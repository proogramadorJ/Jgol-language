package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditorScreen(navController: NavController) {
    MaterialTheme {
        MainContentEditorScreen()
    }

}

@Preview
@Composable
fun MainContentEditorScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.DarkGray
    ) {
        Text(text = "Hello i am second screen")
    }
}