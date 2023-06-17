package org.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class VariableLengthRecording {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024;

    private boolean isRecording = false;
    private ByteArrayOutputStream recordingBuffer;

    public void startRecording() {
        try {
            AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);

            recordingBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            targetDataLine.start();

            isRecording = true;
            System.out.println("Recording started. Press 'Q' to stop.");

            while (isRecording) {
                bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                recordingBuffer.write(buffer, 0, bytesRead);
            }

            targetDataLine.stop();
            targetDataLine.close();

            System.out.println("Recording stopped.");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        isRecording = false;
    }

    public byte[] getRecording() {
        return recordingBuffer.toByteArray();
    }

    public static void main(String[] args) {
        VariableLengthRecording recorder = new VariableLengthRecording();

        Thread recordingThread = new Thread(() -> {
            recorder.startRecording();
        });
        recordingThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Q")) {
                recorder.stopRecording();
                break;
            }
        }

        byte[] recording = recorder.getRecording();
        System.out.println("Recording length: " + recording.length + " bytes.");
    }
}
