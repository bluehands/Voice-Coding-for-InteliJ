package com.jetbrains.rider.plugins.voiceCodingPlugin.evaluation.evaluationActions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.rider.plugins.voiceCodingPlugin.evaluation.BatchRecorder

class StartBatchRecordingAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        Thread {BatchRecorder.startBatchRecording()}.start()
    }
}


class StopBatchRecordingAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        BatchRecorder.stopBatchRecording()
    }
}

class PerformBatchedAction: AnAction() {
    private var lastActionPerformed = 0
    override fun actionPerformed(p0: AnActionEvent) {
        when (BatchRecorder.handleBatchInput(++lastActionPerformed)) {
            -1 -> lastActionPerformed = 0
            0 -> lastActionPerformed--
        }
    }

}