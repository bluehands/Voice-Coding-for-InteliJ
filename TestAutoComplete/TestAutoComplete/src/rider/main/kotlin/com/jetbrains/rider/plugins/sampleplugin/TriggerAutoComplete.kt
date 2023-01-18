package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class TriggerAutoComplete : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val project = event.project
        val popupController = project?.let { AutoPopupController.getInstance(it) }
        TestObject.toggleBool()
        popupController?.scheduleAutoPopup(editor)
    }
}