package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


object VoiceController {
    private var codingMode = false
    private var verbatimMode = false
    var listeningMode = false
    var controllerActive = false

    private val subscriptionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/SubscriptionKey.txt").readText()
    private val regionKey = File("C:/Users/Public/Documents/VoiceCodingPlugin/RegionKey.txt").readText()
    private const val audioFileName = "C:/Users/Public/Documents/VoiceCodingPlugin/BufferFile.wav"
    private val speechConfig = SpeechConfig.fromSubscription(subscriptionKey, regionKey)
    private val audioConfig = AudioConfig.fromWavFileInput(audioFileName)
    //private val audioConfig = AudioConfig.fromDefaultMicrophoneInput()

    //Turning off coding mode also disables verbatim mode
    fun toggleCodingMode() {
        codingMode = !codingMode
        verbatimMode = verbatimMode && codingMode
    }

    //Entering verbatim mode also enters coding mode
    fun toggleVerbatimMode() {
        verbatimMode = !verbatimMode
        codingMode = verbatimMode || codingMode
    }

    fun startListening() = runBlocking {
        listeningMode = true
        val microphoneHandler = MicrophoneHandler()
        val audioInputStream = microphoneHandler.startAudioInputStream()
        launch {
            while (true) {
                delay (50)
                if (microphoneHandler.detectNoise(audioInputStream)) break
            }
            microphoneHandler.startRecording(audioFileName, audioInputStream)
            if (!codingMode) {
                IntentHandler.recognizeIntent(speechConfig, audioConfig)
            }
            else {
                SpeechHandler.startTranscription(speechConfig, audioConfig, verbatimMode)
            }
        }
        listeningMode = false
    }
}