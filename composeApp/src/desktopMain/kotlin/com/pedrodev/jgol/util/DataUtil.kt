package com.pedrodev.jgol.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DataUtil {
    fun getCurrentFormattedTime(currentTime : LocalDateTime?): String {
        val currentDateTime  = currentTime ?: LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")
        return currentDateTime.format(formatter)
    }
}