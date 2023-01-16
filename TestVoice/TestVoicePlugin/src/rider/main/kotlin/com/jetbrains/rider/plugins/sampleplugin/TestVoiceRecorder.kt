package com.jetbrains.rider.plugins.sampleplugin
import com.intellij.openapi.ui.Messages
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.math.sqrt
import kotlin.system.exitProcess


class TestVoiceRecorder(recordingTime: Long, audioFile: File) {
    private val _recordingTime = recordingTime
    private val _audioFile = audioFile
    private val _fileType = AudioFileFormat.Type.WAVE
    private val _recordingThreshold = 1
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

    private fun startRecording(audioInputStream: AudioInputStream) {
        AudioSystem.write(audioInputStream, _fileType, _audioFile)
    }

    private fun stopRecording() {
        _line.stop()
        _line.close()
        Messages.showErrorDialog("Finished recording", "Finished")
    }

    fun recordAudio() {
        val stopper = Thread {
            run {
                Thread.sleep(_recordingTime)
                stopRecording()
            }
        }
        val audioInputStream = startAudioInputStream()
        while (true) {
            if (detectNoise(audioInputStream)) break
        }
        stopper.start()
        startRecording(audioInputStream)
        return
    }

    private fun detectNoise(audioInputStream: AudioInputStream): Boolean {
        val buffer = ByteArray(5000)
        audioInputStream.read(buffer)
        return calculateVolumeLevelRMS(buffer) > _recordingThreshold
    }

    private fun calculateVolumeLevelRMS(audioBuffer: ByteArray): Double {
        val elementCount = audioBuffer.size
        var squareSum = 0.0
        for (i in audioBuffer) {
            squareSum += i * i
        }
        return sqrt((squareSum / elementCount))
    }


    private fun startAudioInputStream(): AudioInputStream {
        val format = getAudioFormat()

        val info = DataLine.Info(TargetDataLine::class.java, format)

        if (!AudioSystem.isLineSupported(info)) {
            Messages.showErrorDialog("Line not supported", "Error")
            exitProcess(0)
        }
        _line = AudioSystem.getLine(info) as TargetDataLine
        _line.open(format)
        _line.start()

        return AudioInputStream(_line)
    }

}