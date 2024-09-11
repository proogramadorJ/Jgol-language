package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pedrodev.jgol.interpreter.AstPrinter
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
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
    println("Running code")
    AstPrinter.test()
}