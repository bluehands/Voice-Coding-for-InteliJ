package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.jetbrains.rider.test.scriptingApi.insertString
import com.jetbrains.rider.test.scriptingApi.moveToOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestParallelAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        testFun(p0)
    }

    private fun testFun(event: AnActionEvent) = runBlocking {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val offset: Int = if (editor?.caretModel != null) editor.caretModel.offset else 0
        val popupController = event.project?.let { AutoPopupController.getInstance(it) }
        launch {
            delay(500)
            popupController?.scheduleAutoPopup(editor)
        }
        editor?.insertString("i")
        editor?.moveToOffset(offset + 1 )

    }
}