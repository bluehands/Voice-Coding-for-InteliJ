package com.jetbrains.rider.plugins.voiceCodingPlugin.security

import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rider.plugins.voiceCodingPlugin.UserParameters

class SetAzureKeysAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val dialog = AzureKeysDialog()
        dialog.show()
        if (dialog.isOK) {
            PasswordSafe.instance.setPassword(UserParameters.credentialKeyAttribute, dialog.keyField.text)
            PasswordSafe.instance.setPassword(UserParameters.credentialRegionAttribute, dialog.regionField.text)
            UserParameters.updateAzureKeys()
        }
    }
}