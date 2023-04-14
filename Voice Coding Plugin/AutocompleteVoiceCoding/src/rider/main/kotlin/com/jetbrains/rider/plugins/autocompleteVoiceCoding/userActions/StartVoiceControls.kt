package com.jetbrains.rider.plugins.autocompleteVoiceCoding.userActions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.jetbrains.rider.plugins.autocompleteVoiceCoding.UserParameters
import com.jetbrains.rider.plugins.autocompleteVoiceCoding.VoiceController

class StartVoiceControls: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        if (UserParameters.azureSubscriptionKey == "" || UserParameters.azureRegionKey == "") {
            Messages.showErrorDialog("Missing azure speech service.", "Error!")
        }
        else {
            VoiceController.startController()
        }
    }

}