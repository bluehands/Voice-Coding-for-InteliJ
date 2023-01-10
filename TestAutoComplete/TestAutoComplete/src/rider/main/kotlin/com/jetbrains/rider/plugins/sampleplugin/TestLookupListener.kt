package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.codeInsight.lookup.LookupListener
import com.intellij.codeInsight.lookup.LookupManagerListener
import com.intellij.openapi.ui.Messages

class TestLookupManagerListener: LookupManagerListener {
    override fun activeLookupChanged(old: Lookup?, new: Lookup?) {
        val listener = TestLookupListener()
        new?.addLookupListener(listener)
    }

}

class TestLookupListener: LookupListener {
    private lateinit var _lookup: Lookup
    private var currentCount = 0
    override fun lookupShown(event: LookupEvent) {
        _lookup = event.lookup
    }

    override fun uiRefreshed() {
        //wenn sich die Möglichkeiten ändern
        val newCount = _lookup.items.size
        if (newCount != currentCount) currentCount = newCount
        else Messages.showErrorDialog("$currentCount elements found!", "Lookup")
    }
}