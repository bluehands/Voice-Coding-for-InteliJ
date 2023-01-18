package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.File


const val audioFilePath = "E:/TestAudio.wav"
class TestRecordingAction: AnAction() {
    override fun actionPerformed(actionParameters: AnActionEvent) {
        val audioFile = File(audioFilePath)
        val recordingTimeMS: Long = 5000
        val recorderInstance = TestVoiceRecorder(recordingTimeMS, audioFile)
        val recodingThread = Thread {
            recorderInstance.recordAudio()
        }
        recodingThread.start()
    }

}
