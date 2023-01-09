package com.jetbrains.rider.plugins.sampleplugin

import com.google.api.gax.rpc.ApiStreamObserver
import com.google.api.gax.rpc.BidiStreamingCallable
import com.google.cloud.speech.v1.*
import com.google.common.util.concurrent.SettableFuture
import com.google.protobuf.ByteString
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

const val audioFilePath = "F:/TestAudio.wav"
class TestRecordingAction: AnAction() {
    override fun actionPerformed(actionParameters: AnActionEvent) {
        val audioFile = File(audioFilePath)
        val recordingTimeMS: Long = 5000
        val recorderInstance = TestVoiceRecorder(recordingTimeMS, audioFile)
        recorderInstance.recordAudio()
    }
}

class TestTranscribeAudio: AnAction() {
    /**
     * Performs streaming speech recognition on raw PCM audio data.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    @Throws(Exception::class, IOException::class)
    fun streamingRecognizeFile(fileName: String) {
        val path = Paths.get(fileName)
        val data: ByteArray = Files.readAllBytes(path)
        SpeechClient.create().use { speech ->

            // Configure request with local raw PCM audio
            val recConfig: RecognitionConfig = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setLanguageCode("de-DE")
                .setSampleRateHertz(16000)
                .setModel("default")
                .build()
            val config: StreamingRecognitionConfig =
                StreamingRecognitionConfig.newBuilder().setConfig(recConfig).build()

            class ResponseApiStreamingObserver<T> : ApiStreamObserver<T> {
                private val future =
                    SettableFuture.create<List<T>>()
                private val messages: MutableList<T> = ArrayList()
                override fun onNext(message: T) {
                    messages.add(message)
                }

                override fun onError(t: Throwable) {
                    future.setException(t)
                }

                override fun onCompleted() {
                    future.set(messages)
                }

                // Returns the SettableFuture object to get received messages / exceptions.
                fun future(): SettableFuture<List<T>> {
                    return future
                }
            }

            val responseObserver: ResponseApiStreamingObserver<StreamingRecognizeResponse> =
                ResponseApiStreamingObserver<StreamingRecognizeResponse>()
            val callable: BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> =
                speech.streamingRecognizeCallable()
            val requestObserver: ApiStreamObserver<StreamingRecognizeRequest> =
                callable.bidiStreamingCall(responseObserver)

            // The first request must **only** contain the audio configuration:
            requestObserver.onNext(
                StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build()
            )

            // Subsequent requests must **only** contain the audio data.
            requestObserver.onNext(
                StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(ByteString.copyFrom(data))
                    .build()
            )

            // Mark transmission as completed after sending the data.
            requestObserver.onCompleted()
            val responses: List<StreamingRecognizeResponse> = responseObserver.future().get()
            for (response in responses) {
                // For streaming recognize, the results list has one is_final result (if available) followed
                // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
                // Just print the first result here.
                val result: StreamingRecognitionResult = response.getResultsList().get(0)
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                val alternative: SpeechRecognitionAlternative = result.getAlternativesList().get(0)
                Messages.showErrorDialog(alternative.getTranscript(), "Transcription")
                //System.out.printf("Transcript : %s\n", alternative.getTranscript())
            }
        }
    }

    override fun actionPerformed(p0: AnActionEvent) {
        streamingRecognizeFile(audioFilePath)
    }
}