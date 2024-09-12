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
import com.pedrodev.jgol.interpreter.Jgol
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.run_code
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File
import java.nio.file.Files
import java.nio.file.Path


var code : String = ""

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

    var inMemoryCode by remember { mutableStateOf("") }

    if (HomeScreenEditScreenSharedData.isFileSelected) {
        inMemoryCode = Files.readString(HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) })
    }

    // TODO código duplicado na memoria? Otimizar

    TextField(
        textStyle = TextStyle(color = Color.White),
        value = inMemoryCode,
        onValueChange = {
            inMemoryCode = it
            code = it
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
    println("Running code...")
    val jgolInterpreter = Jgol()
    // TODO o código que vai ser executado deve ser o inMemory(Pode ainda não ter sido salvo) ou do arquivo?
    jgolInterpreter.run(code)
}