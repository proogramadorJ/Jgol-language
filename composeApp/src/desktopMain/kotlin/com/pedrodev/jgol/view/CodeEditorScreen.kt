package com.pedrodev.jgol.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.pedrodev.jgol.ide.SessionLogs
import com.pedrodev.jgol.interpreter.Jgol
import com.pedrodev.jgol.shared.HomeScreenEditScreenSharedData
import com.pedrodev.jgol.util.DefaultSource
import com.pedrodev.jgol.util.NotificationUtil
import io.github.vinceglb.filekit.core.FileKit
import jgol.composeapp.generated.resources.Res
import jgol.composeapp.generated.resources.return_arrow
import jgol.composeapp.generated.resources.run_code
import jgol.composeapp.generated.resources.save
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

var navControllerScreeen: NavController? = null

object EditorViewModel : ViewModel() {
    var inMemoryCode by mutableStateOf(if (HomeScreenEditScreenSharedData.isFileSelected) getCodeFromFile() else DefaultSource.code)

    // Terminal
    var outputLines by mutableStateOf(listOf<String>())
    var waitingForInput by mutableStateOf(false)
    var currentInput by mutableStateOf("")
    var enterPressed by mutableStateOf(false) // Flag to track when Enter key is pressed
    var isCodeRunning by mutableStateOf(false) // Flag to track if code is currently running
    var isTerminalMinimized by mutableStateOf(false) // Flag to track if terminal is minimized
    var isTerminalVisible by mutableStateOf(false) // Flag to track if terminal should be visible
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
            MenuBarCompose()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CodeInputEditor(editorViewModel)
            }

            TerminalView(editorViewModel)
        }
    }
}

@Composable
fun CodeInputEditor(editorViewModel: EditorViewModel) {
    val lineCount = editorViewModel.inMemoryCode.lines().size

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color.LightGray),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            Spacer(modifier = Modifier.size(18.dp))
            for (i in 2..lineCount) {
                Text(
                    text = (i - 1).toString(),
                    color = Color.Black,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        TextField(
            textStyle = TextStyle(color = Color.Black, fontSize = 12.sp),
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
fun TerminalView(editorViewModel: EditorViewModel) {
    if (editorViewModel.isTerminalVisible) {
        val scrollState = rememberLazyListState()

        var terminalHeight by remember { mutableStateOf(200f) }

        LaunchedEffect(editorViewModel.outputLines.size) {
            if (editorViewModel.outputLines.isNotEmpty()) {
                scrollState.animateScrollToItem(editorViewModel.outputLines.size - 1)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (editorViewModel.isTerminalMinimized) 40.dp else terminalHeight.dp) // Minimize if needed
                .background(Color.Black)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(Color.DarkGray)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                if (!editorViewModel.isTerminalMinimized) {
                                    terminalHeight = (terminalHeight - dragAmount.y).coerceIn(100f, 500f)
                                }
                            }
                        }
                )

                IconButton(
                    onClick = { 
                        editorViewModel.isTerminalMinimized = !editorViewModel.isTerminalMinimized 
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(
                        text = if (editorViewModel.isTerminalMinimized) "+" else "-",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            if (!editorViewModel.isTerminalMinimized) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.weight(1f)
                ) {
                    items(editorViewModel.outputLines) { line ->
                        val isError = line.contains("Erro") || line.contains("[Linha") || line.contains("linha")
                        Text(
                            text = line,
                            color = if (isError) Color.Red else Color.Green,
                            fontSize = 12.sp
                        )
                    }
                }

                if (editorViewModel.waitingForInput) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ">",
                            color = Color.Green,
                            fontSize = 14.sp
                        )
                        TextField(
                            value = editorViewModel.currentInput,
                            onValueChange = { editorViewModel.currentInput = it },
                            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Black,
                                focusedIndicatorColor = Color.Green,
                                unfocusedIndicatorColor = Color.Green
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    editorViewModel.enterPressed = true
                                }
                            )
                        )
                    }
                }
            }
        }
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

        }
    } else {
        Files.writeString(
            HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) },
            EditorViewModel.inMemoryCode
        )

        SessionLogs.log("Código salvo ${HomeScreenEditScreenSharedData.filePath?.let { Path.of(it) }}")
    }
    HomeScreenEditScreenSharedData.filePath?.let { File(it).name }?.let {
        NotificationUtil.showNotification(
            "Código salvo!",
            it
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun runCode() {
    SessionLogs.log("Executando Código...")
    val jgolInterpreter = Jgol()

    EditorViewModel.isCodeRunning = true
    EditorViewModel.isTerminalVisible = true
    EditorViewModel.isTerminalMinimized = false

    jgolInterpreter.setOutputHandler { message ->
        EditorViewModel.outputLines = EditorViewModel.outputLines + message
    }

    jgolInterpreter.setInputHandler {
        EditorViewModel.waitingForInput = true
        EditorViewModel.enterPressed = false

        while (!EditorViewModel.enterPressed) {
            Thread.sleep(100)
        }

        val input = EditorViewModel.currentInput
        EditorViewModel.waitingForInput = false
        EditorViewModel.currentInput = ""
        EditorViewModel.enterPressed = false
        input
    }

    EditorViewModel.outputLines = listOf()

    GlobalScope.launch {
        jgolInterpreter.run(EditorViewModel.inMemoryCode) {
            EditorViewModel.isCodeRunning = false
        }
    }
}
