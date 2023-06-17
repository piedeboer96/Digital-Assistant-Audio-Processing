package org.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AudioRecorder {
    private static final int THRESHOLD = 1000; // Adjust threshold level as per your requirement
    private static final int BUFFER_SIZE = 4096;
    private static final String OUTPUT_FILE = "recording.wav";

    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Monitoring audio...");

            byte[] buffer = new byte[BUFFER_SIZE];
            boolean isRecording = false;
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                int max = 0;

                for (int i = 0; i < bytesRead; i++) {
                    int value = buffer[i];
                    if (value < 0) {
                        value = -value;
                    }
                    if (value > max) {
                        max = value;
                    }
                }

                if (max > THRESHOLD) {
                    if (!isRecording) {
                        System.out.println("Start recording...");
                        isRecording = true;
                    }
                    outputBuffer.write(buffer, 0, bytesRead);
                } else {
                    if (isRecording) {
                        System.out.println("Stop recording...");
                        break;
                    }
                }
            }

            line.stop();
            line.close();

            System.out.println("Exporting recording to WAV file...");
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(outputBuffer.toByteArray()), format,
                    outputBuffer.size() / format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new java.io.File(OUTPUT_FILE));

            System.out.println("Recording saved to " + OUTPUT_FILE);
            System.exit(0);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
