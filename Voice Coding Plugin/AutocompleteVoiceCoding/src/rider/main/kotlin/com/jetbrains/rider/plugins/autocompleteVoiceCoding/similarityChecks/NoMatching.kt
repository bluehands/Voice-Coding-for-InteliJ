package com.jetbrains.rider.plugins.autocompleteVoiceCoding.similarityChecks

class NoMatching(private val identityString: String): SubstringMatchingInterface {
    override fun getBestSubstring(): String {
        return identityString
    }

    override fun getBestDistance(): Int {
        return 0
    }

    override fun getResultIndex(): Int {
        return 0
    }
}