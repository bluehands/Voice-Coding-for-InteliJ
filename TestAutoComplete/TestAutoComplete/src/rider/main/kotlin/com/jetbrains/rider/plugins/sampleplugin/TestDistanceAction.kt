package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class TestDistanceAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val caret = editor?.caretModel
        val caretOffset = caret?.offset ?: 0
        val psiElement = caretOffset.let { off -> file?.findElementAt(off - 1) }
        val word = "Bruttosozialprodukt"
        val nodeString = psiElement?.node?.text ?: ""
        val similarity = MostSimilarSubstring(word, nodeString)
        Messages.showErrorDialog("The smallest hamming distance of $nodeString to any substring of $word is ${similarity.getBestHammingDistance()}." +
                "The substring is at position ${similarity.getResultIndex()}, the substring is ${similarity.getBestSubstring()}",
            "Hamming Distance")
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