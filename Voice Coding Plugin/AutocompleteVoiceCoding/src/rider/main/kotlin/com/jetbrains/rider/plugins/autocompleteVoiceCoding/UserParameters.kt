package com.jetbrains.rider.plugins.autocompleteVoiceCoding

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.util.PropertiesComponent
import java.io.File

enum class MatchingAlgorithm { None, Hamming, DamerauLevenshtein }
object UserParameters {
    val credentialKeyAttribute = CredentialAttributes("AzureSubscriptionKey")
    val credentialRegionAttribute = CredentialAttributes("AzureRegionKey")
    var documentDirectory = ""
        private set
    const val documentDirectoryName = "documentDirectory"
    var audioFileName = "$documentDirectory/BufferFile.wav"
        private set
    var homophoneFile = "$documentDirectory/Homophones.txt"
        private set
    var azureSubscriptionKey = ""
        private set
    var azureRegionKey = ""
        private set
    const val thresholdName = "recordingThreshold"
    var recordingThreshold = 4
        private set
    const val matchingName = "matchingAlgorithm"
    var matchingAlgorithm = MatchingAlgorithm.None
        private set
    const val useBufferName = "useBuffer"
    var useBufferFile = true
        private set

    init {
        updateAzureKeys()
        loadSettings()
    }

    fun updateAzureKeys() {
        azureSubscriptionKey = PasswordSafe.instance.get(credentialKeyAttribute)?.getPasswordAsString() ?: ""
        azureRegionKey = PasswordSafe.instance.get(credentialRegionAttribute)?.getPasswordAsString() ?: ""
    }

    fun loadSettings(){
        useBufferFile = PropertiesComponent.getInstance().getBoolean(useBufferName)
        matchingAlgorithm = when (PropertiesComponent.getInstance().getInt(matchingName, 0)){
            0 -> MatchingAlgorithm.None
            1 -> MatchingAlgorithm.Hamming
            2 -> MatchingAlgorithm.DamerauLevenshtein
            else -> MatchingAlgorithm.None
        }
        recordingThreshold = PropertiesComponent.getInstance().getInt(thresholdName, 4)
        documentDirectory = PropertiesComponent.getInstance().getValue(documentDirectoryName, "C:/Users/Public/Documents/VoiceCodingPlugin")
        val path = File(documentDirectory)
        if (!path.exists()) path.mkdir()
    }
}