package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import com.pedrodev.jgol.terminal.Terminal
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.run_code
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
        color = Color.Black
    ) {
        Box(
        ) {
            Column {
                MenuBarCompose()
                CodeInputEditor()
            }
        }
    }


    println("was file selected: ${HomeScreenEditScreenSharedData.isFileSelected}")
    println("file path: ${HomeScreenEditScreenSharedData.filePath}" ?: "")
}

@Composable
fun CodeInputEditor() {

    // TODO Se recebeu um arquivo preencher aqui

    var inMemoryCode by remember { mutableStateOf("") }

    TextField(
        textStyle = TextStyle(color = Color.White),
        value = inMemoryCode,
        onValueChange = {
            inMemoryCode = it
        },
        modifier = Modifier.fillMaxSize()
    )


}

@Composable
fun MenuBarCompose() {
    Box {
        Surface(
            modifier = Modifier.height(40.dp).fillMaxWidth(),
            color = Color.DarkGray
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { runCode() },
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .padding(end = 10.dp)
                ) {
                    Icon(
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.run_code),
                        contentDescription = "Run code",
                    )
                }
            }
        }
    }
}

fun runCode() {
    val terminal = Terminal()
    terminal.init()
}