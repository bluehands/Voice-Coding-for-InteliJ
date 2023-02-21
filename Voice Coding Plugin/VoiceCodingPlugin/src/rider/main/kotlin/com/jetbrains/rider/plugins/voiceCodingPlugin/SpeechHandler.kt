package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.jetbrains.rd.platform.util.project
import com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks.Homophones
import com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks.MostHammingSimilarSubstring
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
    private val homophoneChecker = Homophones(UserParameters.homophoneFile)
    private var codeInserted = false
    fun startTranscription(speechConfig: SpeechConfig, audioConfig: AudioConfig, verbatim: Boolean) {
        codeInserted = false
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
                    editor?.caretModel?.moveCaretRelatively(-1, 0, false, false, true)
                    editor?.caretModel?.moveCaretRelatively(1, 0, false, false, true)
                    Logger.write("$popupController will start transcription.")
                    popupController?.scheduleAutoPopup(editor)
                }
            }
        }
    }
    fun insertDictation(autocompleteItems: List<LookupElement>? = null, verbatim: Boolean = true, insertionContext: InsertionContext? = null) {
        codingModeEvent = false
        val speechRecognizer = generateRecognizer(autocompleteItems, verbatim)
        val recognitionTask = speechRecognizer.recognizeOnceAsync()
        val recognitionResult = recognitionTask.get().text
        val transcribedText = if (verbatim)  camelCaseContraction(recognitionResult)
                                else generateCodeFromTranscription(recognitionResult, autocompleteItems, insertionContext)
        Logger.write("Text recognized: $transcribedText.")
        if (transcribedText != "" && transcribedText != " " && !codeInserted)  {
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
                if (context != null) {
                    val editor = context.getData(CommonDataKeys.EDITOR)
                    editor?.insertString(transcribedText)
                    codeInserted = true
                    editor?.caretModel?.moveCaretRelatively(transcribedText.length, 0, false, false, true)
                    val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                    popupController?.scheduleAutoPopup(editor)
                }
            }
        }
        Logger.write("Finished handling speech-to-code input.")
        VoiceController.finishListening()
    }
    private fun generateRecognizer(autocompleteItems: List<LookupElement>?, verbatim: Boolean): SpeechRecognizer {
        val speechRecognizer = SpeechRecognizer(_speechConfig, _audioConfig)
        val phraseListGrammar = PhraseListGrammar.fromRecognizer(speechRecognizer)
        if (verbatim) {
            for (word in basicDeclarations) {
                phraseListGrammar.addPhrase(word)
            }
        }
        else if (autocompleteItems != null) {
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

    private fun generateCodeFromTranscription (transcription: String, autocompleteItems: List<LookupElement>?, context: InsertionContext?, isHomophone: Boolean = false): String {
        var code = ""
        var subStringOccurrences = 0
        var elementIndex = -1
        Logger.write("Formatting $transcription.")
        var transcriptionString = transcription.lowercase()
        transcriptionString = transcriptionString.replace(".", "")
        transcriptionString = transcriptionString.replace("?", "")
        transcriptionString = transcriptionString.replace("!", "")
        val homophoneTranscriptions = if (!isHomophone) compilePossibleHomophones(transcriptionString)
                                        else listOf("")
        transcriptionString = transcriptionString.replace(" ", "")
        Logger.write("Searching for $transcriptionString.")
        if (autocompleteItems != null && context != null && transcriptionString != ""){
            val elementStringList: MutableList<String> = mutableListOf()
            for (index in autocompleteItems.indices) {
                val element = autocompleteItems[index]
                val elementString = element.toString().lowercase()
                if (elementString == transcriptionString) {
                    element.handleInsert(context)
                    codeInserted = true
                    Logger.write("Exact match found. Insert $elementString.")
                    break
                }
                if (elementString.contains(transcriptionString)) {
                    subStringOccurrences++
                    code = element.toString()
                    elementIndex = index
                }
                if (subStringOccurrences == 0) elementStringList.add(elementString)
            }
            if (!codeInserted) {
                Logger.write("$subStringOccurrences occurrences of $transcriptionString found.")
                if (subStringOccurrences > 1) {
                    code = transcriptionString
                }
                else if (subStringOccurrences == 1) {
                    codeInserted = true
                    Logger.write("Directly insert Item matching $transcriptionString.")
                    autocompleteItems[elementIndex].handleInsert(context)
                }
                else {
                    code = findClosestMatch(transcription, elementStringList)
                    Logger.write("Try to match with distance. Best match: $code")
                }
            }
        }
        else if (transcriptionString != "") {
            Logger.write("Error: Empty transcription.")
        }
        else {
            Logger.write("Error: Missing Elements or Context.")
        }
        if (code == "" && homophoneTranscriptions.size > 1 && !codeInserted && !isHomophone) {
            for (homophone in homophoneTranscriptions) {
                code = generateCodeFromTranscription(homophone, autocompleteItems, context, true)
                if (codeInserted) break
            }
        }
        return code
    }

    private fun findClosestMatch (transcription: String, autocompleteStrings: List<String>): String {
        var bestCurrentString = ""
        var currentDistance = 99
        var currentIndex = 99
        for (index in autocompleteStrings.indices) {
            val element = autocompleteStrings[index]
            if (element.length >= transcription.length) {
                val mostSimilarSubstring = MostHammingSimilarSubstring(element, transcription)
                val bestString = mostSimilarSubstring.getBestSubstring()
                val distance = mostSimilarSubstring.getBestHammingDistance()
                val stringIndex = mostSimilarSubstring.getResultIndex()
                if (distance < currentDistance || (distance == currentDistance && stringIndex < currentIndex)) {
                    bestCurrentString = bestString
                    currentDistance = distance
                    currentIndex = stringIndex
                }
            }
        }
        return if (currentDistance <= 1 || currentDistance <= transcription.length / 5) bestCurrentString
                else ""
    }

    private fun compilePossibleHomophones (wordSequence: String): List<String> {
        var resultList = mutableListOf("")
        var partialResult = mutableListOf<String>()
        val wordsWithPermutations: MutableList<List<String>> = mutableListOf()
        for (word in wordSequence.split(" ")) {
            var possibleHomophones = homophoneChecker.getHomophone(word)
            if (possibleHomophones == null) possibleHomophones = listOf(word)
            wordsWithPermutations.add(possibleHomophones)
        }
        for (wordWithPermutations in wordsWithPermutations) {
            for (result in resultList) {
                for (permutation in wordWithPermutations) {
                    partialResult.add(result + permutation)
                }
            }
            resultList = partialResult
            partialResult = mutableListOf()
        }
        return resultList
    }
}