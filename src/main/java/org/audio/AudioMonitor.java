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
 *      once it exceeds a pre-defined threshold (=THRESHOLD_ATTACK)
 *      it starts processing the audio as required (e.g. using SPEECH RECOGNITION)
 *      it keeps processing the text
 *      until the TRESHOLD_RELEASE, it was lower than this value for MIN_SILENCE_DURATION
 */
public class AudioMonitor {
    private static final double THRESHOLD_ATTACK = 0.18;             // Treshold to activate the processing
    private static final double THRESHOLD_RELEASE = 0.05;           // Treshold of silence to stop processing
    private static final double MIN_SILENCE_DURATION = 1;           // Minimum duration of silence to stop recording

    private static SpeechRecognizer recognizer = new SpeechRecognizer(); // Create an instance of SpeechRecognizer


    private boolean recordingStarted; // Flag indicating if the recording has started
    private long silenceStartTimestamp; // Timestamp when the silence started

    public static void main(String[] args) throws LineUnavailableException {
        AudioMonitor audioMonitor = new AudioMonitor();
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
                    // Perform recording operations
                    writeAudioToFile(audioBuffer);
//                    writeAudioToFile(audioBuffer);
                    VisualizeLevel.visualizeLevel(maxAmplitude);  // Update the volume level visualization
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
        silenceStartTimestamp = 0; // Reset the silence start timestamp
    }

    private void writeAudioToFile(float[] audioBuffer) {
        // Perform the recording operations here
        // Write the audio data to a file, process it, etc.
        // This method will be called continuously after the recording starts
        System.out.println("Writing audio to file");

        double currentAmplitude = 0.0;
        for (float sample : audioBuffer) {
            double amplitude = Math.abs(sample);
            currentAmplitude = Math.max(currentAmplitude, amplitude);
        }

        if (currentAmplitude < THRESHOLD_RELEASE) {
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
    }

    private void stopRecording() {
        // Perform any cleanup or finalization required for recording
        // This method will be called once, when the recording stops
        System.out.println("Recording stopped");

        System.out.println("Transcribing the audio file...");
        String transcribedText = recognizer.transcribe(AUDIO_FILE_PATH);
        System.out.println("Transcription: " + transcribedText);

        //TODO:
        // -- stop the program
    }
}
