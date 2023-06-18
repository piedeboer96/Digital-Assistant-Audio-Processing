package org.monitor;

import org.effects.DynamicRangeGate;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import org.javapython.SpeechRecognizer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundLevelDetector {

    // history of transcribed text
    List<String> transcriptions = new ArrayList<>();

    private static final int BLOCK_SIZE = 1024;

    // LoPie Audio Suite
    DynamicRangeGate gate = new DynamicRangeGate(-70, 1000, 16000);
    Converter cnv = new Converter();

    // SpeechRecognition Model
    SpeechRecognizer sr;

    // ZeroBuffer for comparison
    float[] silenceBuffer = new float[1024];

    // Activated
    boolean firstRec = false;

    // Silence counter
    long silenceCounter = 0;

    public void monitorMicAudio() {
        try {

            // Load a SpeechRecognizer model

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

                    // init.
                    if(processedBuffer[0]!=0){
                        firstRec=true;
                        System.out.println("activated");
                    }

                    // stop recording and bounce to wav
                    if(processedBuffer[0]==0 && firstRec){
                        iteration++;
                    }


                    if(iteration>10){
                        try {
                            cnv.makeWAV(recordedBuffers);
                            System.out.println("First rec done..");
                            iteration=0;
                            firstRec=false;

                            //TODO:
                            //  -- transcribe

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
