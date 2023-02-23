package com.jetbrains.rider.plugins.voiceCodingPlugin

import java.io.File

enum class MatchingAlgorithm { Hamming, DamerauLevenshtein }
object UserParameters {
    val azureSubscriptionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/SubscriptionKey.txt").readText()
    val azureRegionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/RegionKey.txt").readText()
    const val audioFileName = "C:/Users/Public/Documents/VoiceCodingPlugin/BufferFile.wav"
    const val homophoneFile = "C:/Users/Public/Documents/VoiceCodingPlugin/Homophones.txt"
    const val recordingThreshold = 4
    val matchingAlgorithm = MatchingAlgorithm.Hamming
}