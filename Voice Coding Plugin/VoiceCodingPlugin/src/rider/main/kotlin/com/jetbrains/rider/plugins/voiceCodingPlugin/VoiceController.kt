package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


object VoiceController {
    var codingMode = false
    var verbatimMode = false
    var listeningMode = false

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
        val microphoneHandler = MicrophoneHandler()
        val audioInputStream = microphoneHandler.startAudioInputStream()
        val audioFileName = "F:/BufferFile.wav"
        launch {
            while (true) {
                delay (500)
                if (microphoneHandler.detectNoise(audioInputStream)) break
            }
            microphoneHandler.startRecording(audioFileName, audioInputStream)
            if (!codingMode) {
                //Messages.showErrorDialog("Recognize intent and perform action", "Intent Recognized")
                IntentHandler.recognizeIntent(audioFileName)
            }
            else if (verbatimMode) {
                Messages.showErrorDialog("Transcribe speech and delete whitespace.", "Verbatim Transcription")
            }
            else {
                Messages.showErrorDialog("Transcribe based on autocomplete suggestion.", "Autocomplete Transcription")
            }
        }
    }
}