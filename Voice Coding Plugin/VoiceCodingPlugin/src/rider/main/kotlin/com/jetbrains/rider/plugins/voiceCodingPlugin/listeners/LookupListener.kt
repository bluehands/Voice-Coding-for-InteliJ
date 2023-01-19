package com.jetbrains.rider.plugins.voiceCodingPlugin.listeners

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.codeInsight.lookup.LookupListener
import com.intellij.openapi.ui.Messages
import com.jetbrains.rider.plugins.voiceCodingPlugin.SpeechHandler

class LookupListener: LookupListener {
    private lateinit var _lookup: Lookup
    private var currentCount = 0

    override fun lookupShown(event: LookupEvent) {
        _lookup = event.lookup
        SpeechHandler.transcriptionEventFlag = false
    }

    override fun uiRefreshed() {
        val newCount = _lookup.items.size
        if (newCount != currentCount) currentCount = newCount
        else SpeechHandler.insertDictation(_lookup.items)
    }
}