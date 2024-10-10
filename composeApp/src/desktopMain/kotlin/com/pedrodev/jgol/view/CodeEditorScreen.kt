package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.pedrodev.jgol.interpreter.Jgol
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import com.pedrodev.jgol.terminal.Terminal
import com.pedrodev.jgol.util.DefaultSource
import io.github.vinceglb.filekit.core.FileKit
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.back
import jgol.composeapp.generated.resources.return_arrow
import jgol.composeapp.generated.resources.run_code
import jgol.composeapp.generated.resources.save
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path

var navControllerScreeen: NavController? = null
/*
 TODO bug -> Quando clicar no new file e depois volta para a tela inicial e tenta carregar um arquivo
 TODO não funciona, está carregando o último código.
*/


object EditorViewModel : ViewModel() {
    var inMemoryCode by mutableStateOf(if (HomeScreenEditScreenSharedData.isFileSelected) getCodeFromFile() else DefaultSource.code)
}

fun getCodeFromFile(): String {
    return Files.readString(HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) })
}

@Composable
fun EditorScreen(navController: NavController) {
    navControllerScreeen = navController
    MaterialTheme {
        MainContentEditorScreen(EditorViewModel)
    }
}


@Composable
fun MainContentEditorScreen(editorViewModel: EditorViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Blue
    ) {
        Box(
        ) {
            Column {
                MenuBarCompose()
                CodeInputEditor(editorViewModel)
            }
        }
    }
}

@Composable
fun CodeInputEditor(editorViewModel: EditorViewModel) {

    var inMemoryCode by remember { mutableStateOf(editorViewModel.inMemoryCode) }

    TextField(
        textStyle = TextStyle(color = Color.White),
        value = inMemoryCode,
        onValueChange = { newCode ->
            inMemoryCode = newCode
            editorViewModel.inMemoryCode = newCode
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically

            ) {
                IconButton(
                    onClick = { back() },
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .padding(end = 10.dp)
                ) {
                    Icon(
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.return_arrow),
                        contentDescription = "Back",
                    )
                }
                IconButton(
                    onClick = { saveCode() },
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .padding(end = 10.dp)
                ) {
                    Icon(
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.save),
                        contentDescription = "Save code",
                    )
                }

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

fun back() {
    HomeScreenEditScreenSharedData.isFileSelected = false
    HomeScreenEditScreenSharedData.filePath = null

    navControllerScreeen?.popBackStack()
}

@OptIn(DelicateCoroutinesApi::class)
fun saveCode() {
    if (!HomeScreenEditScreenSharedData.isFileSelected) {
        GlobalScope.launch {
            val file = FileKit.saveFile(EditorViewModel.inMemoryCode.toByteArray(), "main", "jgol")
            HomeScreenEditScreenSharedData.filePath = file?.path
            HomeScreenEditScreenSharedData.isFileSelected = true
            // TODO incluir logs no arquivo da IDE   println("Code saved")
            // TODO adiconar TOAST
        }
    } else {
        Files.writeString(
            HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) },
            EditorViewModel.inMemoryCode
        )
        // TODO incluir logs no arquivo da IDE   println("Code saved")
    }
}

fun runCode() {
    println("Running code...")
    val jgolInterpreter = Jgol()
    //TODO durante dev utilizar terminal integrado IDE
    //openTerminal()
    jgolInterpreter.run(EditorViewModel.inMemoryCode)

}

fun openTerminal() {
    Terminal().init()
}



