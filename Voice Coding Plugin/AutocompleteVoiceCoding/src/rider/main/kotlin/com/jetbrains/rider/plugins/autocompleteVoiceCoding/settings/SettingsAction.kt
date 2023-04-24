package com.jetbrains.rider.plugins.autocompleteVoiceCoding.settings

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rider.plugins.autocompleteVoiceCoding.UserParameters

class SettingsAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val dialog = SettingsDialog()
        dialog.show()
        if(dialog.isOK){
            val noiseVal = Integer.parseInt(dialog.noiseSelect.value.toString())
            PropertiesComponent.getInstance().setValue(UserParameters.thresholdName, noiseVal, 4)
            PropertiesComponent.getInstance().setValue(UserParameters.matchingName, dialog.matchingAlgo.selectedIndex, 0)
            PropertiesComponent.getInstance().setValue(UserParameters.useBufferName, dialog.recordingCheckbox.isSelected)
            PropertiesComponent.getInstance().setValue(UserParameters.documentDirectoryName, dialog.directoryField.text)
            UserParameters.loadSettings()
        }
    }
}