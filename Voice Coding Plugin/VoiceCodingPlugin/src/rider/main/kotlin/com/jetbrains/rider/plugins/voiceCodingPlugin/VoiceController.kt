package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


object VoiceController {
    private var codingMode = true
    private var verbatimMode = false
    var busyListening = false
        private set
    private var controllerActive = false

    private const val audioFileName = UserParameters.audioFileName
    private val speechConfig = SpeechConfig.fromSubscription(UserParameters.azureSubscriptionKey, UserParameters.azureRegionKey)
    private val audioFileConfig = AudioConfig.fromWavFileInput(audioFileName)
    private val audioMicrophoneConfig = AudioConfig.fromDefaultMicrophoneInput()

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
        busyListening = true
        Logger.write("Prepare to listen.")
        val microphoneHandler = MicrophoneHandler()
        val audioInputStream = microphoneHandler.startAudioInputStream()
        launch {
            Logger.write("Start listening.")
            while (busyListening) {
                delay (50)
                if (microphoneHandler.detectNoise(audioInputStream)) {
                    Logger.write("Noise detected.")
                    break
                }
            }
            if (busyListening) {
                microphoneHandler.startRecording(audioFileName, audioInputStream)
                handleAudioInput(audioFileConfig)
            }
            else {
                microphoneHandler.stopRecording()
                Logger.write("Listening stopped.")
            }
        }
    }

    fun handleAudioInput(audioConfig: AudioConfig = audioMicrophoneConfig) {
        busyListening = true
        if (!codingMode) {
            Logger.write("Try to perform command.")
            IntentHandler.recognizeIntent(speechConfig, audioConfig)
        }
        else {
            Logger.write("Transcribe code.")
            SpeechHandler.startTranscription(speechConfig, audioConfig, verbatimMode)
        }
    }

    private fun stopController(){
        busyListening = false
        controllerActive = false
        Logger.write("Stop voice controls.")
    }

    private fun startController() {
        Logger.write("Start voice controls.")
        controllerActive = true
        val listeningThread = Thread {
            while (controllerActive) {
                if (!busyListening) startListening()
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
        busyListening = false
    }


}