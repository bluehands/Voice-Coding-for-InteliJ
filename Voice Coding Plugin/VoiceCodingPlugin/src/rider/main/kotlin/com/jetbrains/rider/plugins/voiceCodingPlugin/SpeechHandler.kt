package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.jetbrains.rd.platform.util.project
import com.jetbrains.rider.test.scriptingApi.insertString
import com.microsoft.cognitiveservices.speech.PhraseListGrammar
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import java.util.*

object SpeechHandler {
    private lateinit var _speechConfig: SpeechConfig
    private lateinit var _audioConfig: AudioConfig
    var transcriptionEventFlag = false
    fun startTranscription(speechConfig: SpeechConfig, audioConfig: AudioConfig, verbatim: Boolean) {
        _speechConfig = speechConfig
        _audioConfig = audioConfig
        if (verbatim) {
            insertDictation(null)
        }
        else {
            transcriptionEventFlag = true
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
                if (context != null) {
                    val editor = context.getData(CommonDataKeys.EDITOR)
                    val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                    popupController?.scheduleAutoPopup(editor)
                }
            }
        }
    }
    fun insertDictation(autocompleteItems: List<LookupElement>?) {
        val speechRecognizer = generateRecognizer(autocompleteItems)
        val recognitionTask = speechRecognizer.recognizeOnceAsync()
        val recognitionResult = recognitionTask.get()
        val transcribedText = camelCaseContraction(recognitionResult.text)
        DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
            if (context != null) {
                val editor = context.getData(CommonDataKeys.EDITOR)
                val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                editor?.insertString(transcribedText)
                editor?.caretModel?.moveCaretRelatively(transcribedText.length, 0, false, false, true)
                popupController?.scheduleAutoPopup(editor)
            }

        }
    }
    private fun generateRecognizer(autocompleteItems: List<LookupElement>?): SpeechRecognizer {
        val speechRecognizer = SpeechRecognizer(_speechConfig, _audioConfig)
        val phraseListGrammar = PhraseListGrammar.fromRecognizer(speechRecognizer)
        phraseListGrammar.addPhrase("short")
        phraseListGrammar.addPhrase("int")
        phraseListGrammar.addPhrase("long")
        phraseListGrammar.addPhrase("byte")
        phraseListGrammar.addPhrase("float")
        phraseListGrammar.addPhrase("double")
        phraseListGrammar.addPhrase("String")
        phraseListGrammar.addPhrase("char")
        phraseListGrammar.addPhrase("bool")
        phraseListGrammar.addPhrase("object")
        phraseListGrammar.addPhrase("private")
        phraseListGrammar.addPhrase("public")
        phraseListGrammar.addPhrase("static")
        phraseListGrammar.addPhrase("const")
        if (autocompleteItems != null) {
            for (element in autocompleteItems) {
                phraseListGrammar.addPhrase(element.toString())
            }
        }
        return speechRecognizer
    }

    private fun camelCaseContraction(unformattedText: String): String {
        var camelCaseString = unformattedText.split(" ").joinToString(""){ it ->
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
        val firstChar = camelCaseString[0].toString()
        camelCaseString = firstChar.lowercase() + camelCaseString.removePrefix(firstChar)
        return camelCaseString
    }
}