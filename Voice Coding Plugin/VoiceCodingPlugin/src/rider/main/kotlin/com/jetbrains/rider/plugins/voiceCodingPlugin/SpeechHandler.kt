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
    var codingModeEvent = false
    private var basicDeclarations = listOf("short", "int", "long", "byte", "float", "double", "String", "char",
                                        "bool", "object", "private", "public", "static", "const")
    fun startTranscription(speechConfig: SpeechConfig, audioConfig: AudioConfig, verbatim: Boolean) {
        _speechConfig = speechConfig
        _audioConfig = audioConfig
        if (verbatim) {
            Logger.write("Start verbatim transcription.")
            insertDictation()
        }
        else {
            codingModeEvent = true
            Logger.write("Start Autocomplete transcription.")
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
                if (context != null) {
                    val editor = context.getData(CommonDataKeys.EDITOR)
                    val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                    Logger.write("$popupController will start transcription.")
                    popupController?.scheduleAutoPopup(editor)
                }
            }
        }
    }
    fun insertDictation(autocompleteItems: List<LookupElement>? = null, verbatim: Boolean = true) {
        codingModeEvent = false
        val speechRecognizer = generateRecognizer(autocompleteItems)
        val recognitionTask = speechRecognizer.recognizeOnceAsync()
        val recognitionResult = recognitionTask.get()
        val transcribedText = if (verbatim) camelCaseContraction(recognitionResult.text)
                                else generateCodeFromTranscription(recognitionResult.text, autocompleteItems)
        Logger.write("Text recognized: $transcribedText")
        DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
            if (context != null) {
                val editor = context.getData(CommonDataKeys.EDITOR)
                editor?.insertString(transcribedText)
                editor?.caretModel?.moveCaretRelatively(transcribedText.length, 0, false, false, true)
                val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                popupController?.scheduleAutoPopup(editor)
            }
        }
        Logger.write("Finished handling speech-to-code input.")
        VoiceController.finishListening()
    }
    private fun generateRecognizer(autocompleteItems: List<LookupElement>?): SpeechRecognizer {
        val speechRecognizer = SpeechRecognizer(_speechConfig, _audioConfig)
        val phraseListGrammar = PhraseListGrammar.fromRecognizer(speechRecognizer)
        for (word in basicDeclarations) phraseListGrammar.addPhrase(word)
        if (autocompleteItems != null) {
            for (element in autocompleteItems) {
                phraseListGrammar.addPhrase(element.toString())
            }
        }
        return speechRecognizer
    }

    private fun camelCaseContraction(unformattedText: String, lowerCase: Boolean = true): String {
        var camelCaseString = unformattedText.split(" ").joinToString(""){ it ->
            Logger.write("Formatting element $it.")
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
        camelCaseString = camelCaseString.replace(".", "")
        val firstChar = camelCaseString[0].toString()
        camelCaseString = if (lowerCase) firstChar.lowercase() + camelCaseString.removePrefix(firstChar)
                            else firstChar.uppercase() + camelCaseString.removePrefix(firstChar)
        return camelCaseString
    }

    private fun generateCodeFromTranscription (transcription: String, autocompleteItems: List<LookupElement>?): String {
        var code = ""
        var exactMatch = false
        Logger.write("Formatting $transcription.")
        var transcriptionString = transcription.lowercase()
        transcriptionString = transcriptionString.replace(" ", "")
        transcriptionString = transcriptionString.replace(".", "")
        transcriptionString = transcriptionString.replace("?", "")
        transcriptionString = transcriptionString.replace("!", "")
        Logger.write("Searching for $transcriptionString.")
        for (word in basicDeclarations) {
            if (word == transcriptionString) {
                code = word
                exactMatch = true
                Logger.write("Exact match found. Insert $code.")
            }
        }
        if (autocompleteItems != null && !exactMatch){
            var subStringOccurrences = 0
            for (element in autocompleteItems) {
                val elementString = element.toString().lowercase()
                if (elementString == transcriptionString) {
                    code = element.toString()
                    exactMatch = true
                    Logger.write("Exact match found. Insert $code.")
                    break
                }
                if (elementString.contains(transcriptionString)) {
                    subStringOccurrences++
                    code = element.toString()
                }
            }
            Logger.write("$subStringOccurrences occurrences of $transcriptionString found.")
            if (subStringOccurrences > 1 && !exactMatch) {
                code = transcriptionString
            }
        }
        else if (!exactMatch){
            Logger.write("Error: CodingMode but no Lookup-Elements!")
        }
        return code
    }
}