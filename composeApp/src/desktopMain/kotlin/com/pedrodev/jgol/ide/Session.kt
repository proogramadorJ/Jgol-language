package com.pedrodev.jgol.ide

object Session {
    private var sessionNumber: Long = 0

    fun initSession() {
        sessionNumber = System.currentTimeMillis()
        SessionLogs.createLogFile(sessionNumber)
    }
}