package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.jetbrains.rd.platform.util.project
import com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks.DamerauLevenshteinMostSimilarSubstring
import com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks.Homophones
import com.jetbrains.rider.plugins.voiceCodingPlugin.similarityChecks.HammingMostSimilarSubstring
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
    private var insertionContext: InsertionContext? = null
    private var lookupElements: List<LookupElement>? = null
    fun startTranscription(speechConfig: SpeechConfig, audioConfig: AudioConfig, verbatim: Boolean) {
        _speechConfig = speechConfig
        _audioConfig = audioConfig
        if (verbatim) {
            Logger.write("Start verbatim transcription.")
            generateVoiceToCode(true)
        }
        else {
            codingModeEvent = true
            lookupElements = null
            insertionContext = null
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
            Thread {
                while (lookupElements == null || insertionContext == null) {
                    Thread.sleep(500)
                    Logger.write("Waiting for Autocomplete to finish, Lookup null ${lookupElements == null}, Context null ${insertionContext == null}")
                }
                generateVoiceToCode(false)
            }.start()
        }
    }

    fun setAutocompleteResults (autocompleteItems: List<LookupElement>, autocompleteContext: InsertionContext) {
        lookupElements = autocompleteItems
        insertionContext = autocompleteContext
        Logger.write("Setting Elements ${lookupElements!!.size} and Context $insertionContext")
    }

    private fun generateVoiceToCode(verbatim: Boolean) {
        codingModeEvent = false
        val speechRecognizer = generateRecognizer(verbatim)
        val recognitionTask = speechRecognizer.recognizeOnceAsync()
        val recognitionResult = recognitionTask.get().text
        val transcribedText = if (verbatim)  camelCaseContraction(recognitionResult)
                                else generateCodeFromTranscription(recognitionResult)
        Logger.write("Text recognized: $transcribedText.")
        DataManager.getInstance().dataContextFromFocusAsync.onSuccess {context: DataContext? ->
            if (context != null) {
                val editor = context.getData(CommonDataKeys.EDITOR)
                if (transcribedText != "" && transcribedText != " ") {
                    editor?.insertString(transcribedText)
                    editor?.caretModel?.moveCaretRelatively(transcribedText.length, 0, false, false, true)
                }
                val popupController = context.project?.let { AutoPopupController.getInstance(it) }
                popupController?.scheduleAutoPopup(editor)
            }
        }
        Logger.write("Finished handling speech-to-code input.")
        VoiceController.finishListening()
    }
    private fun generateRecognizer(verbatim: Boolean): SpeechRecognizer {
        val speechRecognizer = SpeechRecognizer(_speechConfig, _audioConfig)
        val phraseListGrammar = PhraseListGrammar.fromRecognizer(speechRecognizer)
        if (verbatim) {
            for (word in basicDeclarations) {
                phraseListGrammar.addPhrase(word)
            }
        }
        else if (lookupElements != null) {
            for (element in lookupElements!!) {
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

    private fun generateCodeFromTranscription (transcription: String): String {
        var code = ""
        Logger.write("Formatting $transcription.")
        var formattedString = removePunctuation(transcription).lowercase()
        val homophoneTranscriptions = compilePossibleHomophones(formattedString)
        formattedString = formattedString.replace(" ", "")
        if (formattedString != "") {
            Logger.write("Formatted to $formattedString.")
            val transcriptionStrings = mutableListOf(formattedString)
            homophoneTranscriptions.forEach { homophone ->
                if (homophone != formattedString) {
                    transcriptionStrings.add(homophone)
                    Logger.write("Found homophone $homophone.")
                }
            }
            Logger.write("${transcriptionStrings.size} variants found.")
            for (transcriptionString in transcriptionStrings) {
                Logger.write("Searching for $transcriptionString.")
                if (lookupElements != null && transcriptionString != "") {
                    val autocompleteStrings: List<String> = lookupElements!!.map { it.toString() }
                    val matchIndex = findExactMatch(autocompleteStrings, transcriptionString)
                    if (matchIndex >= 0) {
                        if (insertionContext != null) lookupElements!![matchIndex].handleInsert(insertionContext!!)
                        else code = lookupElements!![matchIndex].toString()
                        Logger.write("Single or exact match found for $transcriptionString.")
                        break
                    }
                    else if (matchIndex == -1) {
                        code = transcriptionString
                        Logger.write("Multiple matches found, insert $transcriptionString.")
                    }
                    else if (matchIndex == -2) {
                        code = findClosestMatch(transcriptionString, autocompleteStrings)
                        Logger.write("Try to match with distance. Best match: $code")
                    }
                    else code = ""
                }
                else if (transcriptionString == "") {
                    Logger.write("Error: Empty transcription.")
                }
                else {
                    Logger.write("Error: Missing Autocomplete-Elements.")
                }
                if (code != "") break
            }
        }
        return code
    }

    private fun removePunctuation (string: String): String {
        var result = string
        result = result.replace(".", "")
        result = result.replace(",", "")
        result = result.replace(";", "")
        result = result.replace("?", "")
        result = result.replace("!", "")
        return result
    }

    private fun findExactMatch (autocompleteItems: List<String>, transcription: String) : Int {
        var subStringOccurrences = 0
        var elementIndex = 0
        for (index in autocompleteItems.indices) {
            val element = autocompleteItems[index]
            val elementString = element.lowercase()
            if (elementString == transcription) {
                subStringOccurrences = 1
                elementIndex = index
                Logger.write("Exact match found. Insert $elementString.")
                break
            }
            else if (elementString.contains(transcription)) {
                subStringOccurrences++
                elementIndex = index
                Logger.write("Substring found in $element.")
            }
        }
        Logger.write("$subStringOccurrences occurrences of $transcription found.")
        if (subStringOccurrences == 1) {
            return elementIndex
        }
        if (subStringOccurrences > 1) {
            return -1
        }
        return -2
    }

    private fun findClosestMatch (transcription: String, autocompleteStrings: List<String>): String {
        var bestCurrentString = ""
        var currentDistance = 999
        var currentIndex = 999
        val maximumDistance = maxOf(1, transcription.length / 5)
        for (index in autocompleteStrings.indices) {
            val element = autocompleteStrings[index]
            if (element.length >= transcription.length) {
                val mostSimilarSubstring = if (UserParameters.matchingAlgorithm == MatchingAlgorithm.DamerauLevenshtein)
                                                DamerauLevenshteinMostSimilarSubstring(element, transcription, maximumDistance)
                                            else HammingMostSimilarSubstring(element, transcription)
                val bestString = mostSimilarSubstring.getBestSubstring()
                val distance = mostSimilarSubstring.getBestDistance()
                val stringIndex = mostSimilarSubstring.getResultIndex()
                if (distance < currentDistance || (distance == currentDistance && stringIndex < currentIndex)) {
                    bestCurrentString = bestString
                    currentDistance = distance
                    currentIndex = stringIndex
                }
            }
        }
        return if (currentDistance <= maximumDistance) bestCurrentString
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