package com.jetbrains.rider.plugins.sampleplugin
import com.intellij.openapi.ui.Messages
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.system.exitProcess


class TestVoiceRecorder(recordingTime: Long, audioFile: File) {
    private val _recordingTime = recordingTime
    private val _audioFile = audioFile
    private val _fileType = AudioFileFormat.Type.WAVE
    private lateinit var _line: TargetDataLine

    private fun getAudioFormat(): AudioFormat {
        val sampleRate = 16000f
        val sampleSizeInBits = 8
        val channels = 2
        val signed = true
        val bigEndian = true
        return AudioFormat(
            sampleRate, sampleSizeInBits,
            channels, signed, bigEndian
        )
    }

    private fun startRecording() {
        val format = getAudioFormat()
        val info = DataLine.Info(TargetDataLine::class.java, format)

        if (!AudioSystem.isLineSupported(info)) {
            Messages.showErrorDialog("Line not supported", "Error")
            exitProcess(0)
        }
        _line = AudioSystem.getLine(info) as TargetDataLine
        _line.open(format)
        _line.start()

        val audioInputStream = AudioInputStream(_line)

        AudioSystem.write(audioInputStream, _fileType, _audioFile)
    }

    private fun stopRecording() {
        _line.stop()
        _line.close()
        Messages.showErrorDialog("Finished recording", "Finished")
    }

    fun recordAudio() {
         val stopper = Thread {
            run() {
                Thread.sleep(_recordingTime)
                stopRecording()
            }
        }
        stopper.start()
        startRecording()
    }

}