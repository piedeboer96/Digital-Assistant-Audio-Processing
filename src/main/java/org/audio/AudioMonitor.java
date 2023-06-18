package org.audio;

import be.tarsos.dsp.*;
import be.tarsos.dsp.io.*;
import be.tarsos.dsp.io.jvm.*;
import org.javapython.SpeechRecognizer;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat.Encoding;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AudioMonitor {
    private static final double THRESHOLD_ATTACK = 0.18;
    private static final double THRESHOLD_RELEASE = 0.05;
    private static final double MIN_SILENCE_DURATION = 1;

    private double rec_start = 0;

    private static SpeechRecognizer recognizer = new SpeechRecognizer();
    private boolean recordingStarted;
    private long silenceStartTimestamp;
    private List<float[]> recordedBuffers;



    public void startMonitoring() throws LineUnavailableException {
        recordedBuffers = new ArrayList<>();

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 0);

        AudioProcessor audioProcessor = new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioBuffer = audioEvent.getFloatBuffer();

                double maxAmplitude = 0.0;


                for (float sample : audioBuffer) {
                    double amplitude = Math.abs(sample);
                    maxAmplitude = Math.max(maxAmplitude, amplitude);
                }

                if (!recordingStarted && maxAmplitude >= THRESHOLD_ATTACK) {
                    recordingStarted = true;
                    startRecording();
                }

                if (recordingStarted) {
                    writeAudioToFile(audioBuffer);
                    VisualizeLevel.visualizeLevel(maxAmplitude);
                }

                return true;
            }

            @Override
            public void processingFinished() {
                stopRecording();
            }
        };

        dispatcher.addAudioProcessor(audioProcessor);

        dispatcher.run();
    }

    public static void main(String[] args) throws LineUnavailableException {
        AudioMonitor audioMonitor = new AudioMonitor();
        audioMonitor.startMonitoring();
    }


    private void startRecording() {
        System.out.println("Recording started");
        silenceStartTimestamp = 0;
    }

    private void writeAudioToFile(float[] audioBuffer) {

        if (recordingStarted) {
            System.out.println("writing.");
            recordedBuffers.add(audioBuffer);
        }
        double currentAmplitude = 0.0;
        for (float sample : audioBuffer) {
            double amplitude = Math.abs(sample);
            currentAmplitude = Math.max(currentAmplitude, amplitude);
        }

        System.out.println("add");


        if (currentAmplitude < THRESHOLD_RELEASE) {
            long currentTimestamp = System.currentTimeMillis();
            if (silenceStartTimestamp == 0) {
                silenceStartTimestamp = currentTimestamp;
            } else if (currentTimestamp - silenceStartTimestamp >= MIN_SILENCE_DURATION * 1000) {
                recordingStarted = false;
                stopRecording();
            }
        } else {
            silenceStartTimestamp = 0;
        }
    }

    private void stopRecording() {
        System.out.println("Recording stopped");
        System.out.println("Number of recorded buffers: " + recordedBuffers.size());

        File outputFile = new File("output.wav");
        List<byte[]> audioData = convertFloatBufferListToByteAudioData(recordedBuffers);
        convertToWAV(audioData, outputFile);

        System.out.println("File written successfully!");
        System.exit(0);
    }




    private List<byte[]> convertFloatBufferListToByteAudioData(List<float[]> recordedBuffers) {
        // Set the audio format based on your requirements
        TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(Encoding.PCM_SIGNED,
                44100, // Sample Rate
                16,    // Bit Depth
                1,     // Channels
                2,     // Frame Size
                44100, // Frame Rate
                false  // Big Endian
        );

        // Create an instance of AudioFloatConverter
        TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(audioFormat);

        List<byte[]> recordedBytes = new ArrayList<>();

        for (float[] buffer : recordedBuffers) {
            byte[] outBuff = new byte[buffer.length * 4];
            converter.toByteArray(buffer, outBuff);
            recordedBytes.add(outBuff);
        }

        return recordedBytes;
    }

    private void convertToWAV(List<byte[]> byteArrayList, File outputFile) {
        // Calculate the total length of the combined byte array
        int totalLength = byteArrayList.stream().mapToInt(arr -> arr.length).sum();

        // Create a combined byte array
        byte[] combinedArray = new byte[totalLength];
        int destPos = 0;
        for (byte[] arr : byteArrayList) {
            System.arraycopy(arr, 0, combinedArray, destPos, arr.length);
            destPos += arr.length;
        }

        // Create a ByteArrayInputStream from the combined byte array
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(combinedArray);

        try {
            // Convert the byte array input stream to a WAV file
            AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat,
                    totalLength / audioFormat.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);

            // Close the input stream
            audioInputStream.close();

            // Optionally, you can also close the byte array input stream
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
