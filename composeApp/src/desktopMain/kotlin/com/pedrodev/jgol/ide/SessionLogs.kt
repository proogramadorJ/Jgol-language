package com.pedrodev.jgol.ide

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object SessionLogs {
    private lateinit var filePath: Path
    private var fileName = "log_session_";
    private lateinit var file: File

    //TODO colocar isso aqui em uma thread separada
    fun log(text: String) {
        try {
            file.appendText(text)
        } catch (error: IOException) {
            println("Erro ao tentar salvar log da sessão.")
        }
    }

    fun createLogFile(session: Long) {
        fileName = fileName.plus(session).plus(".log")
        filePath = Path.of(fileName)
        file = Files.createFile(filePath).toFile()

    }
}