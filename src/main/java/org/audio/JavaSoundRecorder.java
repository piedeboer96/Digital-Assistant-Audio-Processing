package org.audio;

import javax.sound.sampled.*;
import java.io.*;

/**
 * @SOURCE:
 *      A sample program to demonstrate how to record sound in Java
 *      author: www.codejava.net
 */
public class JavaSoundRecorder {
    // path of the wav file

    //TODO:     FIX THIS PATH...
    File wavFile = new File("/Users/piedeboer/Desktop/project-2-2/audio/RecordAudio.wav");

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
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

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording in a separate thread
            Thread recordingThread = new Thread(() -> {
                try {
                    AudioSystem.write(ais, fileType, wavFile);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });
            recordingThread.start();

            // Wait for the program termination
            System.out.println("Press enter to stop recording...");
            System.in.read();

            // Stop recording
            line.stop();
            line.close();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();

        // Start recording
        recorder.start();
    }
}
