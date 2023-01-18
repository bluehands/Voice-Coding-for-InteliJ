package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.openapi.ui.Messages
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import com.microsoft.cognitiveservices.speech.intent.*
import java.io.File

object IntentHandler {
    private val subscriptionKey = File("C:/SubscriptionKey.txt").readText()
    private val regionKey = File("C:/RegionKey.txt").readText()
    private val speechConfig = SpeechConfig.fromSubscription(subscriptionKey, regionKey)

    fun recognizeIntent(audioFileName: String) {
        val modelCollection = ArrayList<LanguageUnderstandingModel>()
        val audioConfig = AudioConfig.fromWavFileInput(audioFileName)
        //val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        val intentRecognizer = IntentRecognizer(speechConfig, audioConfig)
        modelCollection.add(generateMatchingModel())
        intentRecognizer.applyLanguageModels(modelCollection)
        var recognitionResult = intentRecognizer.recognizeOnceAsync().get()
        if (recognitionResult.reason == ResultReason.RecognizedSpeech) {
            Messages.showErrorDialog("Speech recognized.", "Error!")
        }
        if (recognitionResult.reason == ResultReason.RecognizedIntent) {
            val entity = recognitionResult.entities
            when (recognitionResult.intentId) {
                "MOVE" ->
                    if (entity.get("direction") != null) {
                        if (entity.get("lines") != null)
                            Messages.showErrorDialog("Move ${recognitionResult.entities.get("direction")} ${recognitionResult.entities.get("lines")} lines!", "Move!")
                        else
                            Messages.showErrorDialog("Move ${recognitionResult.entities.get("direction")}!", "Move!")
                    }
                "CONTEXT" ->
                    Messages.showErrorDialog("Show context action.", "Context")
                "FILE" ->
                    if (entity.get("fileType") != null && entity.get("fileName") != null) {
                        Messages.showErrorDialog("Create new ${entity.get("fileType")} ${entity.get("fileName")}.", "Create New File")
                    }
            }
        }
        else Messages.showErrorDialog("Nothing recognized.", "Error!")
    }

    private fun generateMatchingModel(): PatternMatchingModel {
        val patternMatchingModel = PatternMatchingModel("command_patterns")

        patternMatchingModel.intents.put(PatternMatchingIntent("MOVE","[Row| Line] {direction} [{lines}] [lines | row]"))
        patternMatchingModel.intents.put(PatternMatchingIntent ("CONTEXT", "[Show] context [action]"))
        patternMatchingModel.intents.put(PatternMatchingIntent("FILE", "[Create | Make] new {fileType} {fileName}"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateIntegerEntity("lines"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateListEntity("direction", PatternMatchingEntity.EntityMatchMode.Strict, "up", "down"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateListEntity("fileType", PatternMatchingEntity.EntityMatchMode.Strict, "class", "interface", "record", "struct", "enum"))

        return patternMatchingModel
    }
}