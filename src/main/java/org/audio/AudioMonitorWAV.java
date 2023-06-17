package org.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import org.javapython.SpeechRecognizer;

import javax.sound.sampled.LineUnavailableException;

/**
 * @DESCRIPTION:
 *      This class monitors input audio.
 *      once it exceeds a pre-defined threshold (=THRESHOLD_ATTACK),
 *      it starts processing the audio as required (e.g., using SPEECH RECOGNITION).
 *      It keeps processing the text
 *      until the threshold is lower than THRESHOLD_RELEASE for MIN_SILENCE_DURATION.
 */
public class AudioMonitorWAV {
    private static final double THRESHOLD_ATTACK = 0.18;             // Threshold to activate the processing
    private static final double THRESHOLD_RELEASE = 0.05;           // Threshold of silence to stop processing
    private static final double MIN_SILENCE_DURATION = 1;           // Minimum duration of silence to stop recording

    private static SpeechRecognizer recognizer = new SpeechRecognizer(); // Create an instance of SpeechRecognizer

    private boolean recordingStarted; // Flag indicating if the recording has started
    private float[] accumulatedBuffer; // Accumulated audio buffer
    private int bufferLength; // Length of the accumulated buffer
    private long silenceStartTimestamp; // Timestamp when the silence started

    public static void main(String[] args) throws LineUnavailableException {
        AudioMonitorWAV audioMonitor = new AudioMonitorWAV();
        audioMonitor.startMonitoring();
    }

    public void startMonitoring() throws LineUnavailableException {
        // Create an AudioDispatcher to process audio
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(16000, 4096, 0);

        // Create an AudioProcessor to monitor the audio input and start recording
        AudioProcessor audioProcessor = new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioBuffer = audioEvent.getFloatBuffer();

                // Check if the audio volume exceeds the threshold
                double maxAmplitude = 0.0;
                for (float sample : audioBuffer) {
                    double amplitude = Math.abs(sample);
                    maxAmplitude = Math.max(maxAmplitude, amplitude);
                }

                if (!recordingStarted && maxAmplitude >= THRESHOLD_ATTACK) {
                    // Start recording
                    recordingStarted = true;
                    startRecording();
                }

                if (recordingStarted) {
                    // Accumulate the audio buffer
                    accumulateBuffer(audioBuffer);
                    VisualizeLevel.visualizeLevel(maxAmplitude);  // Update the volume level visualization
                }

                if (!recordingStarted && maxAmplitude < THRESHOLD_RELEASE && bufferLength > 0) {
                    // Check if the silence duration exceeds the minimum required duration
                    long currentTimestamp = System.currentTimeMillis();
                    if (silenceStartTimestamp == 0) {
                        silenceStartTimestamp = currentTimestamp;
                    } else if (currentTimestamp - silenceStartTimestamp >= MIN_SILENCE_DURATION * 1000) {
                        // Stop recording
                        recordingStarted = false;
                        stopRecording();
                    }
                } else {
                    // Reset the silence start timestamp
                    silenceStartTimestamp = 0;
                }

                return true;
            }

            @Override
            public void processingFinished() {
                // No additional processing required
            }
        };

        // Connect the AudioProcessor to the AudioDispatcher
        dispatcher.addAudioProcessor(audioProcessor);

        // Start the audio monitoring
        dispatcher.run();
    }

    private void startRecording() {
        // Perform any setup or initialization required for recording
        // This method will be called once, when the recording starts
        System.out.println("Recording started");
        accumulatedBuffer = null; // Reset the accumulated buffer
        bufferLength = 0; // Reset the buffer length
        silenceStartTimestamp = 0; // Reset the silence start timestamp
    }

    private void accumulateBuffer(float[] audioBuffer) {
        // Accumulate the audio buffer
        if (accumulatedBuffer == null) {
            // Initialize the accumulated buffer
            accumulatedBuffer = audioBuffer.clone();
            bufferLength = audioBuffer.length;
        } else {
            // Extend the accumulated buffer
            float[] newBuffer = new float[bufferLength + audioBuffer.length];
            System.arraycopy(accumulatedBuffer, 0, newBuffer, 0, bufferLength);
            System.arraycopy(audioBuffer, 0, newBuffer, bufferLength, audioBuffer.length);
            accumulatedBuffer = newBuffer;
            bufferLength += audioBuffer.length;
        }
    }

    private void stopRecording() {
        // Perform any cleanup or finalization required for recording
        // This method will be called once, when the recording stops
        System.out.println("Recording stopped");

        // Write the accumulated buffer to a .WAV file or process it as needed
        if (accumulatedBuffer != null && bufferLength > 0) {
            // TODO: Write the accumulated buffer to a .WAV file or process it as needed
            // writeAudioToFile(accumulatedBuffer);
        }

        accumulatedBuffer = null; // Reset the accumulated buffer
        bufferLength = 0; // Reset the buffer length
    }
}
