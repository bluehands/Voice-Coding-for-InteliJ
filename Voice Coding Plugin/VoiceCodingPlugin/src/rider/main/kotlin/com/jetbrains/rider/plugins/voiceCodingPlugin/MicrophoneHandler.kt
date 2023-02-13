package com.jetbrains.rider.plugins.voiceCodingPlugin

import com.intellij.openapi.ui.Messages
import java.io.File
import javax.sound.sampled.*
import kotlin.math.sqrt
import kotlin.system.exitProcess

class MicrophoneHandler {
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

    fun stopRecording() {
        _line.stop()
        _line.close()
    }
    fun detectNoise(audioInputStream: AudioInputStream): Boolean {
        val buffer = ByteArray(100)
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

    fun startAudioInputStream(): AudioInputStream {
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

    fun startRecording(fileName: String, audioInputStream: AudioInputStream) {
        val stopper = Thread {
            run {
                for (i in 0..10){
                    Thread.sleep(1000)
                    if(!detectNoise(audioInputStream)) break
                }
                stopRecording()
            }
        }
        stopper.start()
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(fileName))
    }
}