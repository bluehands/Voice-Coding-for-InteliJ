package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.codeInsight.lookup.LookupListener
import com.intellij.codeInsight.lookup.LookupManagerListener
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.ui.Messages
import com.jetbrains.rd.platform.util.project

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
        else {
            /*DataManager.getInstance().dataContextFromFocusAsync.onSuccess { context: DataContext? ->
                if (context != null) {
                    val editor = context.getData(CommonDataKeys.EDITOR)
                    editor?.caretModel?.moveCaretRelatively(1,0, false, false, true)
                    editor?.caretModel?.moveCaretRelatively(-1,0, false, false, true)
                }
            }*/
            Messages.showErrorDialog("$currentCount elements found!", "Lookup")
        }
    }
}