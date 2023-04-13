package com.jetbrains.rider.plugins.voiceCodingPlugin.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.jetbrains.rider.plugins.voiceCodingPlugin.MatchingAlgorithm
import com.jetbrains.rider.plugins.voiceCodingPlugin.UserParameters
import java.awt.GridLayout
import javax.swing.*

class SettingsDialog: DialogWrapper(true) {
    private val dialogPanel = JPanel(GridLayout(0,2))
    private val matchingLabel = JLabel("Matching Algorithm")
    private val recordingLabel = JLabel("Record via Buffer File")
    private val noiseThreshold = JLabel("Noise Threshold for Recording")
    private val matchingAlgoList = arrayOf("None", "Hamming", "Damerau-Levenshtein")
    val matchingAlgo = ComboBox(matchingAlgoList)
    val recordingCheckbox = JCheckBox()
    val noiseSelect = JSpinner()

    init {
        title = "Plugin Settings"
        init()
        recordingCheckbox.isSelected = UserParameters.useBufferFile
        matchingAlgo.selectedIndex = when(UserParameters.matchingAlgorithm) {
                                            MatchingAlgorithm.None -> 0
                                            MatchingAlgorithm.Hamming -> 1
                                            MatchingAlgorithm.DamerauLevenshtein -> 2
                                    }
        noiseSelect.value = UserParameters.recordingThreshold
        dialogPanel.add(matchingLabel)
        dialogPanel.add(matchingAlgo)
        dialogPanel.add(recordingLabel)
        dialogPanel.add(recordingCheckbox)
        dialogPanel.add(noiseThreshold)
        dialogPanel.add(noiseSelect)
    }
    override fun createCenterPanel(): JComponent {
        return dialogPanel
    }
}