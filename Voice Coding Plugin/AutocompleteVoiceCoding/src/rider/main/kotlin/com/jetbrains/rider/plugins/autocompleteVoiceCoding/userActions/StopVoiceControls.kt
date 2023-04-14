package com.jetbrains.rider.plugins.autocompleteVoiceCoding.userActions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rider.plugins.autocompleteVoiceCoding.VoiceController

class StopVoiceControls: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        VoiceController.stopController()
    }
}