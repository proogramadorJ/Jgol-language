package com.pedrodev.jgol.ide

import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object SessionLogs {
    private lateinit var filePath: Path
    private var fileName = "log_session_"
    private lateinit var file: File

    private suspend fun logAsync(text: String) {
        withContext(Dispatchers.IO) { // Escopo temporário para operações de I/O
            try {
                file.appendText("$text - ${System.currentTimeMillis()}\n")
                println("Log salvo: $text")
            } catch (error: IOException) {
                println("Erro ao tentar salvar log da sessão: ${error.message}")
            }
        }
    }

    fun log(text: String) = runBlocking {
        logAsync(text) // Executa com um escopo temporário no contexto de runBlocking
    }

    fun createLogFile(session: Long) {
        fileName = fileName.plus(session).plus(".log")
        filePath = Path.of(fileName)
        file = Files.createFile(filePath).toFile()
    }
}