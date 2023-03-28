package com.jetbrains.rider.plugins.voiceCodingPlugin.evaluation

import com.jetbrains.rider.plugins.voiceCodingPlugin.Logger
import com.jetbrains.rider.plugins.voiceCodingPlugin.MicrophoneHandler
import com.jetbrains.rider.plugins.voiceCodingPlugin.UserParameters
import com.jetbrains.rider.plugins.voiceCodingPlugin.VoiceController
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

object BatchRecorder {

    private const val batchDirectory = UserParameters.batchAudioDirectory
    private var recording = false

    fun startBatchRecording() = runBlocking  {
        recording = true
        Logger.write("Prepare batch recording.")
        val microphoneHandler = MicrophoneHandler()
        launch {
            Logger.write("Start listening.")
            var i = 0
            while (recording) {
                if (microphoneHandler.busy) delay(500)
                else {
                    val audioInputStream = microphoneHandler.startAudioInputStream()
                    i++
                    while (recording) {
                        delay(50)
                        if (microphoneHandler.detectNoise(audioInputStream)) {
                            Logger.write("Noise detected.")
                            break
                        }
                    }
                    if (recording) {
                        var filename = "$batchDirectory/$i.wav"
                        while (File(filename).exists()) {
                            Logger.write("File $i exists, trying ${i + 1}")
                            filename = "$batchDirectory/${++i}.wav"
                        }
                        Logger.write("Recording batch file $i.")
                        microphoneHandler.startRecording(filename, audioInputStream)
                    }
                    else {
                        microphoneHandler.stopRecording()
                    }
                }
            }
        }
    }

    fun handleBatchInput(i: Int = 1): Int {
        Logger.write("Trying to handle File $i.")
        val batchFile = "$batchDirectory/$i.wav"
        if (!File(batchFile).exists()) {
            Logger.write("File $i does not exist.")
            return -1
        }
        if (!VoiceController.busyListening) {
            val audioConfig = AudioConfig.fromWavFileInput(batchFile)
            VoiceController.handleAudioInput(audioConfig)
            return 1
        }
        Logger.write("Voice Controller is busy. Try again later.")
        return 0
    }

    fun stopBatchRecording() {
        recording = false
    }
}