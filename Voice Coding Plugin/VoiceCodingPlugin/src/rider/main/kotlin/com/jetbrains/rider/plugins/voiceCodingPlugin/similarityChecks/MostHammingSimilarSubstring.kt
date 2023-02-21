package com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks

class MostHammingSimilarSubstring (private val primaryString: String, private val compareSubstring: String) {
    private var bestSubstring = ""
    private var bestHammingDistance = 99
    private var resultIndex = 0
    private val primaryStringLength = primaryString.length
    private val compareSubstringLength = compareSubstring.length

    init {
        if (compareSubstringLength in 1..primaryStringLength) {
            findBestSubstring()
        }
    }

    fun getBestSubstring(): String {
        return bestSubstring
    }

    fun getBestHammingDistance(): Int {
        return bestHammingDistance
    }

    fun getResultIndex(): Int {
        return resultIndex
    }
    private fun findBestSubstring() {
        val maxSubstringIndex = primaryStringLength - compareSubstringLength
        for (i in 0..maxSubstringIndex) {
            val currentSubstring = primaryString.substring(i, i + compareSubstringLength)
            val currentHammingDistance = calculateStringHammingDistance(currentSubstring, compareSubstring)
            if (currentHammingDistance < bestHammingDistance) {
                bestHammingDistance = currentHammingDistance
                bestSubstring = currentSubstring
                resultIndex = i
            }
        }
    }
    private fun calculateStringHammingDistance (string1: String, string2: String): Int {
        val longestString: String
        val shortestString: String
        if (string1.length > string2.length) {
            longestString = string1
            shortestString = string2
        }
        else {
            longestString = string2
            shortestString = string1
        }
        val maxStringIndex = shortestString.length - 1
        var hammingDistance = longestString.length
        for (i in 0 ..maxStringIndex) {
            if (longestString[i] == shortestString[i]) hammingDistance--
        }
        return hammingDistance
    }
}