package com.jetbrains.rider.plugins.autocompleteVoiceCoding.similarityChecks

interface SubstringMatchingInterface {
    fun getBestSubstring(): String
    fun getBestDistance(): Int
    fun getResultIndex(): Int
}