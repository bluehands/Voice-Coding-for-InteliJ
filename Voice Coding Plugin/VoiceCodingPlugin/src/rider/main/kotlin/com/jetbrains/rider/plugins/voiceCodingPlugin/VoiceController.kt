package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


object VoiceController {
    private var codingMode = true
    private var verbatimMode = false
    private var listeningMode = false
    private var controllerActive = false

    private const val audioFileName = UserParameters.audioFileName
    private val speechConfig = SpeechConfig.fromSubscription(UserParameters.azureSubscriptionKey, UserParameters.azureRegionKey)
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

    private fun startListening() = runBlocking {
        listeningMode = true
        Logger.write("Prepare to listen.")
        val microphoneHandler = MicrophoneHandler()
        val audioInputStream = microphoneHandler.startAudioInputStream()
        launch {
            Logger.write("Start listening.")
            while (listeningMode) {
                delay (50)
                if (microphoneHandler.detectNoise(audioInputStream)) {
                    Logger.write("Noise detected.")
                    break
                }
            }
            if (listeningMode) {
                microphoneHandler.startRecording(audioFileName, audioInputStream)
                if (!codingMode) {
                    Logger.write("Try to perform command.")
                    IntentHandler.recognizeIntent(speechConfig, audioConfig)
                }
                else {
                    Logger.write("Transcribe code.")
                    SpeechHandler.startTranscription(speechConfig, audioConfig, verbatimMode)
                }
            }
            else {
                microphoneHandler.stopRecording()
                Logger.write("Listening stopped.")
            }
        }
    }

    private fun stopController(){
        listeningMode = false
        controllerActive = false
        Logger.write("Stop voice controls.")
    }

    private fun startController() {
        Logger.write("Start voice controls.")
        controllerActive = true
        val listeningThread = Thread {
            while (controllerActive) {
                if (!listeningMode) startListening()
                Thread.sleep(500)
            }
        }
        listeningThread.start()
    }

    fun toggleController(){
        if (controllerActive) stopController()
        else startController()
    }

    fun finishListening() {
        listeningMode = false
    }
}