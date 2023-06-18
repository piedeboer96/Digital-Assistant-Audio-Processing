package org.monitor;

import org.effects.DynamicRangeGate;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import org.javapython.SpeechRecognizer;
import org.javapython.TranscriptionService;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SoundLevelDetector {

    private static final int BLOCK_SIZE = 1024;

    // LoPie Audio Suite
    DynamicRangeGate gate = new DynamicRangeGate(-70, 1000, 16000);
    Converter cnv = new Converter();

    // SpeechRecognition Model
    SpeechRecognizer sr;

    // ZeroBuffer for comparison
    float[] silenceBuffer = new float[1024];

    public void monitorMicAudio() {
        try {

            // Load Mozilla DeepSpeech Model (because wav2vec is too slow)


            // Recorded material to analyze using SR
            ArrayList<float[]> recordedBuffers = new ArrayList<>();

            // Define the mic as input stream of audio
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            TargetDataLine line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();

            // Use AudioDispatcher for block size 1024
            int overlap = 0;
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(BLOCK_SIZE, overlap);

            // Measure the loudness using a processor and display in real-time
            dispatcher.addAudioProcessor(new AudioProcessor() {

                float[] processedBuffer = new float[BLOCK_SIZE];
                long iteration = 0;

                @Override
                public boolean process(AudioEvent audioEvent) {
                    // Current buffer
                    float[] audioBuffer = audioEvent.getFloatBuffer();

                    // Apply the gate
                    processedBuffer = gate.process(audioBuffer);

                    // Append the recorded list
                    recordedBuffers.add(Arrays.copyOf(processedBuffer, processedBuffer.length));

                    iteration++;
//                    System.out.println("it " + iteration);

                    // Take care of recording... as long as not silent for > 1 sec
                    // then we transcribe and compare.
                    while(!Arrays.equals(processedBuffer, silenceBuffer)){
                        System.out.println("Audio...");

                    }

                    try {
                        cnv.makeWAV(recordedBuffers);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (iteration > 200) {

                        try {

                            // convert to WAV
                            cnv.makeWAV(recordedBuffers);

                            // trim silence
                            // ....

                            // TODO:
                            // - use a mozilla deepspeech or facebook wav2VEC
                            // (with python) to transcribe the text in "out16.wav"


                            // transcribe
                            System.out.println("transcript");

                            iteration=0;
//                            System.exit(0);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return true;
                }

                @Override
                public void processingFinished() {
                }
            });

            new Thread(dispatcher::run).start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SoundLevelDetector detector = new SoundLevelDetector();
        detector.monitorMicAudio();
    }
}
