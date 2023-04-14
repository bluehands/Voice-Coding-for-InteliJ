package com.jetbrains.rider.plugins.autocompleteVoiceCoding.similarityChecks

import java.io.File

class Homophones (homophoneListFile: String) {
    private val homophoneList: MutableList<List<String>> = mutableListOf()

    init {
        val homophoneFile = File(homophoneListFile)
        if (homophoneFile.exists()) {
            val homophoneSource = homophoneFile.inputStream()
            homophoneSource.bufferedReader().forEachLine {
                val firstChar = it[0]
                if (firstChar != '%') {
                    val homophone = it.split(",")
                    if (homophone.size > 1) homophoneList.add(homophone)
                }
            }
        }
    }

    fun getHomophone (word: String): List<String>? {
        for (homophones in homophoneList) {
            for (homophone in homophones) {
                if (word.lowercase() == homophone) return homophones
            }
        }
        return null
    }
}