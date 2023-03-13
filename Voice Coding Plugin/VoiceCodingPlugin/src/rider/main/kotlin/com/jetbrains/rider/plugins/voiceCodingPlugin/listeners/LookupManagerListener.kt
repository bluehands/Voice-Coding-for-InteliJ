package com.jetbrains.rider.plugins.voiceCodingPlugin.listeners

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupManagerListener
import com.jetbrains.rider.plugins.voiceCodingPlugin.SpeechHandler

class LookupManagerListener: LookupManagerListener {
        override fun activeLookupChanged(old: Lookup?, new: Lookup?) {
            val listener = LookupListener()
            if (SpeechHandler.codingModeEvent) new?.addLookupListener(listener)
    }
}