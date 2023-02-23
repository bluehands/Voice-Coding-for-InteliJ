package com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks

interface SubstringMatchingInterface {
    fun getBestSubstring(): String
    fun getBestDistance(): Int
    fun getResultIndex(): Int
}