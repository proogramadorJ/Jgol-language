package com.pedrodev.jgol.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
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
import java.io.*
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Primeira área ocupando 80% do espaço disponível
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) {
                Column {
                    MenuBarCompose()
                    CodeInputEditor(editorViewModel)
                }
            }

            // Segunda área ocupando os 20% restantes para o terminal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
            ) {
                JetpackComposeSwingTerminal()
            }
        }
    }
}


@Composable
fun JetpackComposeSwingTerminal() {
    javax.swing.SwingUtilities.invokeLater {
        // Criação do JFrame
        val frame = javax.swing.JFrame("Jetpack Compose Terminal")
        frame.defaultCloseOperation = javax.swing.JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 600)

        // Criação do JPanel com o terminal
        val terminalPanel = javax.swing.JPanel().apply {
            layout = java.awt.BorderLayout()

            val jediTermWidget = JediTermWidget(DefaultSettingsProvider())

            // Configuração do terminal
          //  jediTermWidget.setTerminalSettingsProvider(DefaultSettingsProvider())
           redirectSystemStreamsToTerminal(jediTermWidget)
           // jediTermWidget.ttyConnector = StreamTtyConnector(System.`in`, System.out)
            jediTermWidget.start()

            // Adiciona o terminal ao painel
            add(jediTermWidget, java.awt.BorderLayout.CENTER)
        }

        // Adiciona o JPanel ao JFrame
        frame.contentPane = terminalPanel

        // Torna o JFrame visível
        frame.isVisible = true
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

}
private class ExampleTtyConnector(writer: PipedWriter) : TtyConnector {

    private val myReader: PipedReader

    init {
        myReader = try {
            PipedReader(writer)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun close() {
        // Implementação vazia
    }

    override fun getName(): String? {
        return null
    }

    @Throws(IOException::class)
    override fun read(buf: CharArray, offset: Int, length: Int): Int {
        return myReader.read(buf, offset, length)
    }

    override fun write(bytes: ByteArray) {
        // Implementação vazia
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun write(string: String) {
        // Implementação vazia
    }

    override fun waitFor(): Int {
        return 0
    }

    @Throws(IOException::class)
    override fun ready(): Boolean {
        return myReader.ready()
    }
}
const val ESC = 27.toChar()

@Throws(IOException::class)
private fun writeTerminalCommands(writer: PipedWriter) {
    writer.write("$ESC%G")
    writer.write("$ESC[31m")
    writer.write("Terminal initialized\r\n")
  //  writer.write("$ESC[32;43m")
//    writer.write("World\r\n")
}
fun redirectSystemStreamsToTerminal(terminalWidget: JediTermWidget) {
    val terminalOutput = terminalWidget.terminalTextBuffer // Saída do terminal
    val terminalInput = PipedInputStream() // Entrada do terminal
    val terminalOutputStream = PipedOutputStream(terminalInput)

    // Configure o TtyConnector para conectar os fluxos
    val ttyConnector = StreamTtyConnector(terminalInput, terminalOutputStream)
    terminalWidget.ttyConnector = ttyConnector

    // Redirecione os fluxos do sistema
    System.setOut(PrintStream(terminalOutputStream, true)) // Saída padrão
    System.setErr(PrintStream(terminalOutputStream, true)) // Saída de erro
    System.setIn(terminalInput) // Entrada padrão
}
class StreamTtyConnector(
    private val inputStream: InputStream,
    private val outputStream: OutputStream
) : TtyConnector {

    override fun read(buffer: CharArray, offset: Int, length: Int): Int {
        val byteBuffer = ByteArray(length)
        val bytesRead = inputStream.read(byteBuffer, 0, length)
        if (bytesRead == -1) return -1 // End of stream
        val chars = String(byteBuffer, 0, bytesRead).toCharArray()
        System.arraycopy(chars, 0, buffer, offset, chars.size)
        return chars.size
    }

    override fun write(data: ByteArray) {
        outputStream.write(data)
        outputStream.flush()
    }

    override fun write(string: String?) {
        //
    }

    override fun isConnected(): Boolean = true

    override fun waitFor(): Int {
        return 0
    }

    override fun ready(): Boolean {
        return try {
            inputStream.available() > 0
        } catch (e: IOException) {
            false // Retorna falso se houver algum problema com o fluxo
        }
    }

    override fun getName(): String? {
        return null
    }


    override fun close() {
        inputStream.close()
        outputStream.close()
    }
}
