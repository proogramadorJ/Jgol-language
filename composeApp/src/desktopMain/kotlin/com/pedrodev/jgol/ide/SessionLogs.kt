package com.pedrodev.jgol.ide

import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object SessionLogs {
    private lateinit var filePath: Path
    private var fileName = "log_session_";
    private lateinit var file: File
    private val scope = CoroutineScope(Dispatchers.IO)

    private fun logAsync(text: String) {
        scope.launch {
            try {
                file.appendText(text + "\n")
            } catch (error: IOException) {
                println("Erro ao tentar salvar log da sessão.")
            }
        }

    }

    private fun close() {
        scope.cancel()
    }

    fun log(text: String) = runBlocking {
        logAsync(text)
        close()
    }

    fun createLogFile(session: Long) {
        fileName = fileName.plus(session).plus(".log")
        filePath = Path.of(fileName)
        file = Files.createFile(filePath).toFile()

    }
}