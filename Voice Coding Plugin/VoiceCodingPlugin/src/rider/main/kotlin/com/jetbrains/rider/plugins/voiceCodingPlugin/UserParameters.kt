package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe

enum class MatchingAlgorithm { None, Hamming, DamerauLevenshtein }
object UserParameters {
    //val azureSubscriptionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/SubscriptionKey.txt").readText()
    //val azureRegionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/RegionKey.txt").readText()
    var azureSubscriptionKey = ""
        private set
    var azureRegionKey = ""
        private set
    const val audioFileName = "/BufferFile.wav"
    const val homophoneFile = "/Homophones.txt"
    const val recordingThreshold = 4
    val matchingAlgorithm = MatchingAlgorithm.None
    val credentialKeyAttribute = CredentialAttributes("AzureSubscriptionKey")
    val credentialRegionAttribute = CredentialAttributes("AzureRegionKey")
    val useBufferFile = true

    init {
        updateAzureKeys()
    }

    fun updateAzureKeys() {
        azureSubscriptionKey = PasswordSafe.instance.get(credentialKeyAttribute)?.getPasswordAsString() ?: ""
        azureRegionKey = PasswordSafe.instance.get(credentialRegionAttribute)?.getPasswordAsString() ?: ""
    }
}