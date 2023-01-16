package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

class TestTranscription: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val speechConfig = SpeechConfig.fromSubscription("Key", "westeurope")
        val result = fromFile(speechConfig)
        Messages.showErrorDialog("Voice transcribed: $result","Transcription")
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun fromFile(speechConfig: SpeechConfig?): String? {
        val audioConfig = AudioConfig.fromWavFileInput("E:/TestAudio.wav")
        val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        val task: Future<SpeechRecognitionResult> = recognizer.recognizeOnceAsync()
        val result: SpeechRecognitionResult = task.get()
        return result.text
    }
}