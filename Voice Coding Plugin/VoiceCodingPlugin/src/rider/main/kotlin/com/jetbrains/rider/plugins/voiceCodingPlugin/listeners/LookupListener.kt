package com.jetbrains.rider.plugins.voiceCodingPlugin.listeners

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.codeInsight.lookup.LookupListener
import com.jetbrains.rdclient.editors.getPsiFile
import com.jetbrains.rider.plugins.voiceCodingPlugin.Logger
import com.jetbrains.rider.plugins.voiceCodingPlugin.SpeechHandler
import kotlin.properties.Delegates

class LookupListener: LookupListener {
    private var _lookup: Lookup? = null
    private var _completionChar by Delegates.notNull<Char>()
    private var currentCount = 0

    override fun lookupShown(event: LookupEvent) {
        _lookup = event.lookup
        _completionChar = event.completionChar

    }

    override fun uiRefreshed() {
        val newCount = _lookup?.items?.size ?: 0
        if (newCount != currentCount) {
            currentCount = newCount
        } else if (currentCount != 0 && _lookup != null) {
            Logger.write("Loaded autocomplete options, $currentCount found")
            val editor = _lookup!!.editor
            val file = editor.getPsiFile()
            val offset = OffsetMap(editor.document)
            val elementArray: Array<LookupElement> = _lookup!!.items.toTypedArray()
            val context = InsertionContext(offset, _completionChar, elementArray, file!!, editor, true)
            SpeechHandler.insertDictation(_lookup!!.items, false, context)
        }
    }
}