package com.pedrodev.jgol.ide

import com.pedrodev.jgol.util.DataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object SessionLogs {
    private lateinit var filePath: Path
    private var fileName = "log_session_"
    private lateinit var file: File

    private suspend fun logAsync(text: String) {
        withContext(Dispatchers.IO) {
            try {
                file.appendText("$text - ${DataUtil.getCurrentFormattedTime(null)}\n")
            } catch (error: IOException) {
                println("Erro ao tentar salvar log da sessão: ${error.message}")
            }
        }
    }

    fun log(text: String) = runBlocking {
        logAsync(text)
    }

    fun createLogFile(session: Long) {
        fileName = fileName.plus(session).plus(".log")
        filePath = Path.of(fileName)
        file = Files.createFile(filePath).toFile()
    }
}