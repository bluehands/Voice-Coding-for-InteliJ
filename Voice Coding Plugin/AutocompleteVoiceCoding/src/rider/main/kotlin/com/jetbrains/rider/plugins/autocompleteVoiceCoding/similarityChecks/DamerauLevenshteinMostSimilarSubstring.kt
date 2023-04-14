package com.jetbrains.rider.plugins.autocompleteVoiceCoding.similarityChecks

class DamerauLevenshteinMostSimilarSubstring (private val primaryString: String, private val compareSubstring: String,
                                            private val maximumDistance: Int) : SubstringMatchingInterface {
    private var bestSubstring = ""
    private var bestDistance = 99
    private var resultIndex = 0
    private val primaryStringLength = primaryString.length
    private val compareStringLength = compareSubstring.length

    init {
        if ( compareStringLength in 1..primaryStringLength) findBestSubstringAnyLength()
        else if (compareStringLength - primaryStringLength in 1 .. maximumDistance) {
            bestSubstring = primaryString
            bestDistance = calculateDistance(primaryString, compareSubstring)
        }
    }
    override fun getBestSubstring (): String {
        return bestSubstring
    }

    override fun getBestDistance (): Int {
        return bestDistance
    }

    override fun getResultIndex (): Int {
        return resultIndex
    }

    private fun findBestSubstringAnyLength () {
        for (additionalLength in 0 .. maximumDistance) {
            findBestSubstringFixedLength(additionalLength + compareStringLength)
        }
    }

    private fun findBestSubstringFixedLength (length: Int) {
        val maximumIndex = primaryStringLength - length
        if (maximumIndex < 0) return
        for (index in 0 .. maximumIndex) {
            val currentSubstring =  primaryString.substring(index, index + length)
            val currentDistance = calculateDistance(currentSubstring, compareSubstring)
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance
                bestSubstring = currentSubstring
                resultIndex = index
            }
        }
    }

    private fun calculateDistance (string1: String, string2: String): Int {
        val length1 = string1.length
        val length2 = string2.length
        val matrix = Array(length1 + 1) { IntArray(length2 +1 ) }
        for (index1 in 0 .. length1) {
            matrix[index1][0] = index1
        }
        for (index2 in 0 .. length2) {
            matrix[0][index2] = index2
        }
        for (index1 in 1 .. length1) {
            for (index2 in 1 .. length2) {
                val char1 = string1[index1 - 1]
                val char2 = string2[index2 - 1]
                val cost = if (char1 == char2) 0
                            else 1
                matrix[index1][index2] = minOf( matrix[index1 - 1][index2] +1,
                                                matrix[index1][index2 -1] + 1,
                                                matrix [index1 - 1][index2 -1] + cost)
                if (index1 > 1 && index2 > 1) {
                    val previousChar1 = string1[index1 - 2]
                    val previousChar2 = string2[index2 - 2]
                    if (char1 == previousChar2 && char2 == previousChar1)
                        matrix[index1][index2] = minOf(matrix[index1][index2], matrix[index1 - 2][index2 - 2] + 1)
                }
            }
        }
        return matrix[length1][length2]
    }
}