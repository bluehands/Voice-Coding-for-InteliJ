package com.jetbrains.rider.plugins.autocompleteVoiceCoding

import java.io.File

object Logger {
    fun write (log: String){
        val file = File("${UserParameters.documentdir}/Log.txt")
        file.appendText("\n$log")
    }
}