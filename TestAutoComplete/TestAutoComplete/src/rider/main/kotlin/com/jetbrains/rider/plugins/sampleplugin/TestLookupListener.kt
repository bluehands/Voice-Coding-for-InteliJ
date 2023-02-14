package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.editor.Editor
import com.jetbrains.rdclient.editors.getPsiFile
import kotlin.properties.Delegates

class TestLookupManagerListener: LookupManagerListener {
    override fun activeLookupChanged(old: Lookup?, new: Lookup?) {
        val listener = TestLookupListener()
        new?.addLookupListener(listener)
    }

}

class TestLookupListener: LookupListener {
    private var _lookup: Lookup? = null
    private var _completionChar by Delegates.notNull<Char>()
    private var currentCount = 0
    override fun lookupShown(event: LookupEvent) {
        _lookup = event.lookup
        _completionChar = event.completionChar
    }

    override fun uiRefreshed() {
        //wenn sich die Möglichkeiten ändern
        val newCount = _lookup?.items?.size ?: 0
        if (newCount != currentCount) currentCount = newCount
        else if (currentCount > 0 && _lookup != null) {
            /*DataManager.getInstance().dataContextFromFocusAsync.onSuccess { context: DataContext? ->
                if (context != null) {
                    val editor = context.getData(CommonDataKeys.EDITOR)
                    editor?.caretModel?.moveCaretRelatively(1,0, false, false, true)
                    editor?.caretModel?.moveCaretRelatively(-1,0, false, false, true)
                }
            }*/
            val editor = _lookup!!.editor
            val file = editor.getPsiFile()
            val offset = OffsetMap(editor.document)
            val elementArray: Array<LookupElement> = _lookup!!.items.toTypedArray()
            val context = InsertionContext(offset, _completionChar, elementArray, file!!, editor, true)
            _lookup!!.items[0].handleInsert(context)
            //Messages.showErrorDialog("$currentCount elements found!", "Lookup")
        }
    }
}