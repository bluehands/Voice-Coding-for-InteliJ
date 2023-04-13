package com.jetbrains.rider.plugins.voiceCodingPlugin.userActions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rider.plugins.voiceCodingPlugin.VoiceController

class StartVoiceControls: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        VoiceController.startController()
    }

}