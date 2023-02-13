package com.jetbrains.rider.plugins.voiceCodingPlugin.listeners

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupManagerListener
import com.jetbrains.rider.plugins.voiceCodingPlugin.Logger
import com.jetbrains.rider.plugins.voiceCodingPlugin.SpeechHandler

class LookupManagerListener: LookupManagerListener {
        override fun activeLookupChanged(old: Lookup?, new: Lookup?) {
            Logger.write("Lookup Event!")
            val listener = LookupListener()
            if (SpeechHandler.codingModeEvent) new?.addLookupListener(listener)
    }
}