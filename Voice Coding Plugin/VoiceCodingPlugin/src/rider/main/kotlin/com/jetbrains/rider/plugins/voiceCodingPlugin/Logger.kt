package com.jetbrains.rider.plugins.voiceCodingPlugin

import java.io.File

object Logger {
    private const val logFile = "C:/Users/Public/Documents/VoiceCodingPlugin/Log.txt"
    fun write (log: String){
        val file = File(logFile)
        file.appendText("\n$log")
    }
}