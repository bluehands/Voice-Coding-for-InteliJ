package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import java.io.File


class TestDistanceAction: AnAction() {

    private val homophoneChecker = Homophones("C:/Users/Public/Documents/VoiceCodingPlugin/Homophones.txt")
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val caret = editor?.caretModel
        val caretOffset = caret?.offset ?: 0
        val psiElement = caretOffset.let { off -> file?.findElementAt(off - 1) }
        val word = "Bruttosozialprodukt"
        val nodeString = psiElement?.node?.text ?: ""
        val similarity = MostSimilarSubstring(word, nodeString)
        val damerauLevenshtein = DamerauLevenshteinMostSimilarSubstring(word, nodeString, 99)
        val homophones = compilePossibleHomophones("List exceed all beer")
        //for (homophone in homophones) File("C:/Users/Public/Documents/VoiceCodingPlugin/Log.txt").appendText("$homophone \n")
        Messages.showErrorDialog("The smallest Damerau-Levenshtein distance of $nodeString to any substring of $word is ${damerauLevenshtein.getBestDistance()}. " +
                "The maximum allowed distance is ${nodeString.length / 5}. " +
                "The substring is at position ${damerauLevenshtein.getResultIndex()}, the substring is ${damerauLevenshtein.getBestSubstring()}.",
            "Damerau-Levenshtein Distance")
        Messages.showErrorDialog("The smallest hamming distance of $nodeString to any substring of $word is ${similarity.getBestHammingDistance()}. " +
                "The maximum allowed distance is ${nodeString.length / 5}. " +
                "The substring is at position ${similarity.getResultIndex()}, the substring is ${similarity.getBestSubstring()}.",
            "Hamming Distance")
    }

    private fun compilePossibleHomophones (wordSequence: String): List<String> {
        var resultList = mutableListOf("")
        var partialResult = mutableListOf<String>()
        val wordsWithPermutations: MutableList<List<String>> = mutableListOf()
        for (word in wordSequence.split(" ")) {
            var possibleHomophones = homophoneChecker.getHomophone(word)
            if (possibleHomophones == null) possibleHomophones = listOf(word)
            wordsWithPermutations.add(possibleHomophones)
        }
        for (wordWithPermutations in wordsWithPermutations) {
            for (result in resultList) {
                for (permutation in wordWithPermutations) {
                    partialResult.add(result + permutation)
                }
            }
            resultList = partialResult
            partialResult = mutableListOf()
        }
        return resultList
    }
}

class MostSimilarSubstring (private val primaryString: String, private val compareSubstring: String) {
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



class Homophones (homophoneListFile: String) {
    private val homophoneList: MutableList<List<String>> = mutableListOf()

    init {
        val homophoneSource = File(homophoneListFile).inputStream()
        homophoneSource.bufferedReader().forEachLine {
            val firstChar = it[0]
            if (firstChar != '%') {
                var homophone = it.split(",")
                if (homophone.size > 1) homophoneList.add(homophone)
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

class DamerauLevenshteinMostSimilarSubstring (private val primaryString: String, private val compareSubstring: String,
                                              private val maximumDistance: Int) {
    private var bestSubstring = ""
    private var bestDistance = 99
    private var resultIndex = 0
    private val primaryStringLength = primaryString.length
    private val compareStringLength = compareSubstring.length

    init {
        if ( compareStringLength in 1..primaryStringLength) findBestSubstringAnyLength()
    }
    fun getBestSubstring (): String {
        return bestSubstring
    }

    fun getBestDistance (): Int {
        return bestDistance
    }

    fun getResultIndex (): Int {
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