package org.monitor;

import org.effects.DynamicRangeGate;
import org.monitor.*;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.util.fft.FFT;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class SoundLevelDetector {


    boolean monitoring = true;      // monitoring

    boolean recording = false;      // recording

    boolean gate_open = false;


    // LoPi Audio FX Suite
    DynamicRangeGate gate = new DynamicRangeGate(-80,1000,16000);
    Recorder rec = new Recorder();




    JFrame frame;
    JLabel loudnessLabel;



    public void monitorMicAudio() {
        try {

            // Recorded material to analyze using SR
            ArrayList<float[]> recordedBuffers = new ArrayList<>();

            // Define the mic as input stream of audio
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            TargetDataLine line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();

            // Use AudioDispatcher for block size 1024
            int blockSize = 1024;
            int overlap = 0;
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(1024, 0);

            // Measure the loudness using a processor and display in real-time
            dispatcher.addAudioProcessor(new AudioProcessor() {

                float[] processedBuffer = new float[1024];

                @Override
                public boolean process(AudioEvent audioEvent) {

                    // currrent buffer
                    float[] audioBuffer = audioEvent.getFloatBuffer();

                    // apply the gate :)
                    processedBuffer =  gate.process(audioBuffer);

                    //System.out.println(Arrays.toString(processedBuffer));




                    return true;
                }



                @Override
                public void processingFinished() {
                }
            });


            //TODO:
            // - visualize the peakLevel in a simple swing gui
            // - visualize it by making a vertical bar graph
            // - where the 'volume peaks'

            // Create and configure the GUI
//            frame = new JFrame("Sound Level Detector");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setLayout(new BorderLayout());
//
//            loudnessLabel = new JLabel("Loudness: ");
//            frame.add(loudnessLabel, BorderLayout.CENTER);
//            frame.pack();
//            frame.setVisible(true);

            // Start audio processing


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
