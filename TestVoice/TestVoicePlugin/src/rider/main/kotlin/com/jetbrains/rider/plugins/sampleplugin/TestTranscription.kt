package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future


class TestTranscription: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val speechConfig = SpeechConfig.fromSubscription(File("C:/SubscriptionKey.txt").readText(), File("C:/RegionKey.txt").readText())
        val result = recognizeFromMicrophone(speechConfig)//fromFile(speechConfig)
        Messages.showErrorDialog("Voice transcribed: $result","Transcription")
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun fromFile(speechConfig: SpeechConfig?): String? {
        val audioConfig = AudioConfig.fromWavFileInput("F:/TestAudio.wav")

        //val audioConfig = AudioConfig.fromDefaultMicrophoneInput()


        val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        val task: Future<SpeechRecognitionResult> = recognizer.recognizeOnceAsync()
        val result: SpeechRecognitionResult = task.get()
        return result.text
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun recognizeFromMicrophone(speechConfig: SpeechConfig?): String {
        val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        val speechRecognizer = SpeechRecognizer(speechConfig, audioConfig)

        val task = speechRecognizer.recognizeOnceAsync()
        val speechRecognitionResult = task.get()
        if (speechRecognitionResult.reason == ResultReason.RecognizedSpeech) {
            return "RECOGNIZED: Text= ${speechRecognitionResult.text}"
        } else if (speechRecognitionResult.reason == ResultReason.NoMatch) {
            return "NOMATCH: Speech could not be recognized."
        }
        else if (speechRecognitionResult.reason == ResultReason.Canceled) {
            val cancellation = CancellationDetails.fromResult(speechRecognitionResult)

            return if (cancellation.reason == CancellationReason.Error) {
                "CANCELED: ErrorCode= ${ cancellation.errorCode }  ErrorDetails= ${ cancellation.errorDetails }"
            } else {
                "CANCELED: Reason= + ${cancellation.reason}"
            }
        }
        return "Uuups"
    }
}