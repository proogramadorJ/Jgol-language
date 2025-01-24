package com.pedrodev.jgol.ide

import java.time.LocalDateTime
import java.time.ZoneOffset

object Session {
    private var sessionNumber: Long = 0

    fun initSession() {
        sessionNumber = System.currentTimeMillis()
        SessionLogs.createLogFile(sessionNumber)
    }
}