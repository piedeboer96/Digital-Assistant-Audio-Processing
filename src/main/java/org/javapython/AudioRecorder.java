package org.javapython;

import javax.sound.sampled.*;
import java.io.*;

public class AudioRecorder {

    // record duration, in milliseconds
    static final long RECORD_TIME = 5000;  // 5 seconds

    // path of the wav file
    File wavFile = new File("Recording.wav");

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Recording audio...");

            AudioInputStream ais = new AudioInputStream(line);

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    void finish() {
        line.stop();
        line.close();
        System.out.println("Recording finished.");
    }

    public static void main(String[] args) {
        final AudioRecorder recorder = new AudioRecorder();

        // Creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            recorder.finish();
            // After recording is finished, we start the transcription
            SpeechRecognizer recognizer = new SpeechRecognizer();
            System.out.println("Transcribing the audio file...");
            String transcribedText = recognizer.transcribe("Recording.wav");
            System.out.println("Transcription: " + transcribedText);
        });

        stopper.start();

        // Start recording
        recorder.start();
    }
}
