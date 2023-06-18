package org.monitor;

import org.effects.DynamicRangeGate;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import org.javapython.SpeechRecognizerV2;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class SoundLevelDetector {

    // History of transcribed text
    Stack<String> conversation = new Stack<>();

    private static final int BLOCK_SIZE = 1024;

    // LoPie Audio Suite
    DynamicRangeGate gate = new DynamicRangeGate(-50, 1000, 16000);
    Converter cnv = new Converter();

    // SpeechRecognition Model
    SpeechRecognizerV2 sr;

    // Threshold passed for first ti,e
    boolean firstRec = false;

    // Silence counter (extra gate control)
    long silenceCounter = 10;

    public void monitorMicAudio() {

        // Load a SpeechRecognizer model
        sr = new SpeechRecognizerV2();

        try {
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

                // Count silence duration additional to gate release
                long silentIter = 0;

                @Override
                public boolean process(AudioEvent audioEvent) {
                    // Current buffer
                    float[] audioBuffer = audioEvent.getFloatBuffer();

                    // Apply the gate
                    processedBuffer = gate.process(audioBuffer);

                    // Append the recorded list
                    recordedBuffers.add(Arrays.copyOf(processedBuffer, processedBuffer.length));

                    // Activate
                    if(processedBuffer[0]!=0){
                        firstRec=true;
                        System.out.println("activated");
                    }

                    // Accumulate for stop recording later
                    if(processedBuffer[0]==0 && firstRec){
                        silentIter++;
                    }

                    if(silentIter>silenceCounter){
                        try {

                            // TODO:
                            // - (optional) trim pre_post silence

                            // Make .WAV from recorded buffers
                            cnv.makeWAV(recordedBuffers);
                            System.out.println("First rec done..");

                            // Reset bookkeeping
                            silentIter=0;
                            firstRec=false;

                            // Transcribe
                            String transcribedText = sr.transcribe("out16.wav");

                            // TODO:
                            //  - identify speaker

                            conversation.push(transcribedText);
                            recordedBuffers.clear();

                        } catch (IOException e) {
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

    public void close() {
        sr.close();
    }

    public static void main(String[] args) {
        SoundLevelDetector detector = new SoundLevelDetector();
        detector.monitorMicAudio();
        // make sure to close the speech recognizer at the end
        Runtime.getRuntime().addShutdownHook(new Thread(detector::close));
    }
}
