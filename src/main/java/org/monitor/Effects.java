//package org.monitor;
//
//import be.tarsos.dsp.SilenceDetector;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class Effects {
//
//    float[] zeroBuffer = new float[1024];
//
//    Timer timer;
//    boolean isTimerRunning;
//
//    /**
//     * Returns the dBSPL for a buffer.
//     */
//    private double soundPressureLevel(final float[] buffer) {
//        double power = 0.0;
//
//        for (float element : buffer) {
//            power += element * element;
//        }
//
//        double value = Math.sqrt(power) / buffer.length;
//        return 20.0 * Math.log10(value);
//    }
//
//    /*
//     * Dynamic Range Gate that lets audio through or blocks it with variable release time
//     */
////    public float[] dynamicRangeGate(float[] buffer, double threshold, double releaseTime){
////
////
////        double dbSPL = soundPressureLevel(buffer);
////
////        System.out.println("level" + dbSPL);
////
////
////
////        if (dbSPL > threshold) {
////            // Step 1: Let the original buffer[] through
////            System.out.println("gate open");
////            return buffer;
////        } else {
////            isTimerRunning = true;
////            timer = new Timer();
////
////            if (dbSPL > threshold) {
////                // Step 1: Let the original buffer[] through
////                System.out.println("gate open");
////                return buffer;
////            } else {
////                // Step 2: Start the timer and let audio through for releaseTime
////                if (!isTimerRunning) {
////                    isTimerRunning = true;
////                    timer = new Timer();
////                    timer.schedule(new TimerTask() {
////                        @Override
////                        public void run() {
////                            // Step 3: Block audio after releaseTime
////                            System.out.println("gate closed");
////                            timer.cancel();
////                            isTimerRunning = false;
////                        }
////                    }, (long) releaseTime);
////
////                    // Return the buffer while the timer is running
////                    System.out.println("release time..");
////                    return buffer;
////                } else {
////                    // Timer is already running, return zero buffer
////                    return zeroBuffer;
////                }
////
////            }
////
////        }
////    }
//
//
//
//    public float[] drGate(float[] buffer, double threshold, double releaseTime){
//
//    }
//
////    public float[] gate(float[] audioBuffer, double silenceThreshold) {
////
////        SilenceDetector silenceDetector = new SilenceDetector(silenceThreshold, true);
////        float[] outputBuffer = new float[audioBuffer.length];
////
////        boolean isSilent = silenceDetector.isSilence(audioBuffer, silenceThreshold);
////        if (isSilent) {
////            for (int i = 0; i < audioBuffer.length; i++) {
////                outputBuffer[i] = 0.0f;
////            }
////        } else {
//////            System.out.println("gate open");
////            System.arraycopy(audioBuffer, 0, outputBuffer, 0, audioBuffer.length);
////        }
////
////        return outputBuffer;
////    }
//
////    public double peakLevelMeter(float[] audioBuffer) {
////        double maxAmplitude = 0.0;
////
////        for (float sample : audioBuffer) {
////            double amplitude = Math.abs(sample);
////            maxAmplitude = Math.max(maxAmplitude, amplitude);
////        }
////
////        return  maxAmplitude;
////
////    }
//
//    //
////    public double startRecording(float[] audioBuffer){
////
////        // TODO:
////        // - convert the file in real time to .WAV using tarsosDSP
////
////    }
//
//}
