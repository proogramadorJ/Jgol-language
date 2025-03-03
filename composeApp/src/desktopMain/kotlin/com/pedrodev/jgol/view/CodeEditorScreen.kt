package com.pedrodev.jgol.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.pedrodev.jgol.ide.SessionLogs
import com.pedrodev.jgol.interpreter.Jgol
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import com.pedrodev.jgol.util.DefaultSource
import io.github.vinceglb.filekit.core.FileKit
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.return_arrow
import jgol.composeapp.generated.resources.run_code
import jgol.composeapp.generated.resources.save
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path

var navControllerScreeen: NavController? = null

object EditorViewModel : ViewModel() {
    var inMemoryCode by mutableStateOf(if (HomeScreenEditScreenSharedData.isFileSelected) getCodeFromFile() else DefaultSource.code)
}

fun getCodeFromFile(): String {
    return Files.readString(HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) })
}

@Composable
fun EditorScreen(navController: NavController) {
    navControllerScreeen = navController
    EditorViewModel.inMemoryCode = if (HomeScreenEditScreenSharedData.isFileSelected)
        getCodeFromFile()
    else
        DefaultSource.code
    MaterialTheme {
        MainContentEditorScreen(EditorViewModel)
    }
}

@Composable
fun MainContentEditorScreen(editorViewModel: EditorViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.LightGray
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Primeira área ocupando 80% do espaço disponível
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Column {
                    MenuBarCompose()
                    CodeInputEditor(editorViewModel)
                }
            }

        }
    }
}

/**
@Composable
fun CodeInputEditor(editorViewModel: EditorViewModel) {

    TextField(
        textStyle = TextStyle(color = Color.White),
        value = editorViewModel.inMemoryCode,
        onValueChange = { newCode ->
            editorViewModel.inMemoryCode = newCode
        },
        modifier = Modifier.fillMaxSize()
    )


}
**/
@Composable
fun CodeInputEditor(editorViewModel: EditorViewModel) {
    val lineCount = editorViewModel.inMemoryCode.lines().size

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Coluna para exibir os números das linhas
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
                .padding(4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            for (i in 2..lineCount) {
                Text(
                    text = (i - 1).toString(),
                    color = Color.Black,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        // Campo de texto para editar o código
        TextField(
            textStyle = TextStyle(color = Color.Black),
            value = editorViewModel.inMemoryCode,
            onValueChange = { newCode ->
                editorViewModel.inMemoryCode = newCode
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
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
    SessionLogs.log("Navegando para tela inicial.")
}

@OptIn(DelicateCoroutinesApi::class)
fun saveCode() {
    if (!HomeScreenEditScreenSharedData.isFileSelected) {
        GlobalScope.launch {
            val file = FileKit.saveFile(EditorViewModel.inMemoryCode.toByteArray(), "main", "jgol")
            HomeScreenEditScreenSharedData.filePath = file?.path
            HomeScreenEditScreenSharedData.isFileSelected = true
            SessionLogs.log("Código salvo ${file?.path}")
            // TODO adiconar Alerta de código salvo
        }
    } else {
        Files.writeString(
            HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) },
            EditorViewModel.inMemoryCode
        )

        SessionLogs.log("Código salvo ${HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) }}")
        HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) }
    }
}

fun runCode() {
    SessionLogs.log("Executando Código...")
    val jgolInterpreter = Jgol()
    jgolInterpreter.run(EditorViewModel.inMemoryCode)
}
