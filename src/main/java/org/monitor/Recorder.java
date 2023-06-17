package org.monitor;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Recorder {
    private static final AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
    private static final int bufferSize = 16000 * 2;

    public void startRecording(float[] audioBuffer) {
        TargetDataLine line;
        try {
            line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat, bufferSize);
            line.start();

            AudioInputStream audioInputStream = new AudioInputStream(line);

            // Create a new thread to write the audio data to a file
            Thread thread = new Thread(() -> {
                File outputFile = new File("funoutput.wav");

                try (AudioInputStream audioStream = new AudioInputStream(audioInputStream, audioFormat,
                        audioBuffer.length / audioFormat.getFrameSize())) {
                    AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();

            // Wait for the recording to complete
            thread.join();

            line.stop();
            line.close();
        } catch (LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
