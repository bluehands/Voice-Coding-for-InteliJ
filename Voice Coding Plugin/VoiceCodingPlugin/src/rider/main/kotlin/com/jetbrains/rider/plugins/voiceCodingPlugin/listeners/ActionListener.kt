package com.jetbrains.rider.plugins.voiceCodingPlugin.listeners

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.jetbrains.rider.plugins.voiceCodingPlugin.VoiceController

class ActionListener: AnActionListener {
    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        super.afterActionPerformed(action, event, result)
        if (VoiceController.controllerActive && !VoiceController.listeningMode) VoiceController.startListening()
    }
}