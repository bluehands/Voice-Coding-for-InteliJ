package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.ui.Messages
import com.jetbrains.rider.util.idea.Caret
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import com.microsoft.cognitiveservices.speech.intent.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object IntentHandler {
    fun recognizeIntent(speechConfig: SpeechConfig, audioConfig: AudioConfig) {
        val modelCollection = ArrayList<LanguageUnderstandingModel>()
        val intentRecognizer = IntentRecognizer(speechConfig, audioConfig)
        modelCollection.add(generateMatchingModel())
        intentRecognizer.applyLanguageModels(modelCollection)
        val recognitionResult = intentRecognizer.recognizeOnceAsync().get()
        when (recognitionResult.reason) {
            ResultReason.RecognizedSpeech -> {
                Messages.showErrorDialog("Speech recognized.", "Error!")
            }
            ResultReason.RecognizedIntent -> {
                val entity = recognitionResult.entities
                when (recognitionResult.intentId) {
                    "MOVE" -> moveIntentExecution(entity.get("direction"), entity.get("distance")?.toInt())
                    "CONTEXT" ->
                        Messages.showErrorDialog("Show context action.", "Context")
                    "FILE" ->
                        if (entity.get("fileType") != null && entity.get("fileName") != null) {
                            val fileName = camelCaseContraction(entity.get("fileName"))
                            val fileType = entity.get("fileType")
                            Messages.showErrorDialog(
                                "Create new $fileType $fileName.","Create New File")
                        }
                }
            }
            else -> Messages.showErrorDialog("Nothing recognized.", "Error!")
        }
    }

    private fun generateMatchingModel(): PatternMatchingModel {
        val patternMatchingModel = PatternMatchingModel("command_patterns")

        patternMatchingModel.intents.put(PatternMatchingIntent("MOVE","[Row | Line | Move] {direction} [{distance}] [lines | rows]"))
        patternMatchingModel.intents.put(PatternMatchingIntent ("CONTEXT", "[Show] context [action]"))
        patternMatchingModel.intents.put(PatternMatchingIntent("FILE", "[Create | Make] new {fileType} {fileName}"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateIntegerEntity("distance"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateListEntity("direction", PatternMatchingEntity.EntityMatchMode.Strict, "up", "down", "left", "right"))
        patternMatchingModel.entities.put(PatternMatchingEntity.CreateListEntity("fileType", PatternMatchingEntity.EntityMatchMode.Strict, "class", "interface", "record", "struct", "enum"))

        return patternMatchingModel
    }

    private fun moveIntentExecution(direction: String?, distance: Int?) {
        if (direction != null) {
            val dist = distance ?: 1
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
                 val caret = context?.getData(CommonDataKeys.EDITOR)?.caretModel

                when (direction) {
                    "up" -> caret?.moveCaretRelatively(0, (-dist),false, false, true)
                    "down" -> caret?.moveCaretRelatively(0, dist,false, false, true)
                    "left" -> caret?.moveCaretRelatively((-dist), 0,false, false, true)
                    "right" -> caret?.moveCaretRelatively(dist, 0,false, false, true)
                }

            }
        }
    }

    private fun camelCaseContraction(unformattedText: String): String {
        val camelCaseString = unformattedText.split(" ").joinToString(""){ it ->
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
        return camelCaseString
    }
}
