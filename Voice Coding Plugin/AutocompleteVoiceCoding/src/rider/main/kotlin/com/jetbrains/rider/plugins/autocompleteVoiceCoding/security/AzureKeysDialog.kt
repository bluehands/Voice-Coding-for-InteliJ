package com.jetbrains.rider.plugins.autocompleteVoiceCoding.security

import com.intellij.openapi.ui.DialogWrapper
import com.jetbrains.rider.plugins.autocompleteVoiceCoding.UserParameters
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField


class AzureKeysDialog: DialogWrapper(true) {
    private val dialogPanel = JPanel(GridLayout(0,2))
    private val keyLabel = JLabel("Azure Subscription Key")
    private val regionLabel = JLabel("Azure Region Key")
    val keyField = JTextField(UserParameters.azureSubscriptionKey)
    val regionField = JTextField(UserParameters.azureRegionKey)
    init {
    title = "Azure Credentials"
    init()
    dialogPanel.add(keyLabel)
    dialogPanel.add(keyField)
    dialogPanel.add(regionLabel)
    dialogPanel.add(regionField)
}
    override fun createCenterPanel(): JComponent {
        return dialogPanel
    }
}
