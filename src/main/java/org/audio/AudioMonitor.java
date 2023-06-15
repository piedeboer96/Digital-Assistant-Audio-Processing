package org.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

import javax.sound.sampled.LineUnavailableException;

public class AudioMonitor {
    private static final double THRESHOLD = 0.1; // Adjust the threshold as needed

    public static void main(String[] args) throws LineUnavailableException {
        // Create an AudioDispatcher to process audio
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 0);

        // Create an AudioProcessor to monitor the audio input and start recording
        AudioProcessor audioMonitor = new AudioProcessor() {
            private boolean recordingStarted = false;

            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioBuffer = audioEvent.getFloatBuffer();

                // Check if the audio volume exceeds the threshold
                double maxAmplitude = 0.0;
                for (float sample : audioBuffer) {
                    double amplitude = Math.abs(sample);
                    maxAmplitude = Math.max(maxAmplitude, amplitude);
                }

                if (!recordingStarted && maxAmplitude >= THRESHOLD) {
                    // Start recording
                    recordingStarted = true;
                    startRecording();
                }

                if (recordingStarted) {
                    // Perform recording operations
                    writeAudioToFile(audioBuffer);
                }

                return true;
            }

            @Override
            public void processingFinished() {
                // No additional processing required
            }
        };

        // Connect the AudioProcessor to the AudioDispatcher
        dispatcher.addAudioProcessor(audioMonitor);

        // Start the audio monitoring
        dispatcher.run();
    }

    private static void startRecording() {
        // Perform any setup or initialization required for recording
        // This method will be called once, when the recording starts
        System.out.println("Recording started");
    }

    private static void writeAudioToFile(float[] audioBuffer) {
        // Perform the recording operations here
        // Write the audio data to a file, process it, etc.
        // This method will be called continuously after the recording starts
        System.out.println("Writing audio to file");
    }
}
