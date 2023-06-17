package org.monitor;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class WavMaker {



    public void exportToWav(ArrayList<float[]> recordedBuffers) {
        // Specify the output file name
        String outputFileName = "myrec.wav";

        // Set the audio format
        AudioFormat audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                16000,   // Sample rate: 16 kHz
                16,      // Sample size in bits: 16 bits
                1,       // Channels: Mono
                2,       // Frame size = 2 bytes (16 bits) * 1 channel
                16000,   // Frame rate = Sample rate
                true     // Big-endian byte order
        );

        // Create the audio file and output stream
        File audioFile = new File(outputFileName);
        AudioInputStream audioStream = new AudioInputStream(
                new SilenceGenerator(recordedBuffers), audioFormat, recordedBuffers.size());

        // Write the audio data to the file
        try {
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
            System.out.println("File exported successfully: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Custom implementation of AudioInputStream that generates audio data from a list of float arrays
    class SilenceGenerator extends AudioInputStream {
        private List<float[]> buffers;
        private int bufferIndex;
        private int bufferLength;
        private int totalFrames;

        public SilenceGenerator(List<float[]> buffers) {
            super(null, null, -1);
            this.buffers = buffers;
            this.bufferIndex = 0;
            this.bufferLength = buffers.get(0).length;
            this.totalFrames = buffers.size() * bufferLength / 2;  // Divide by 2 for 16-bit audio
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (bufferIndex >= buffers.size()) {
                return -1;  // End of stream reached
            }

            int framesToRead = len / 2;  // Divide by 2 for 16-bit audio
            int framesRemaining = totalFrames - (bufferIndex * bufferLength / 2);

            if (framesToRead > framesRemaining) {
                framesToRead = framesRemaining;
            }

            float[] buffer = buffers.get(bufferIndex);
            int bytesRead = 0;

            if (framesToRead > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(framesToRead * 2);  // Multiply by 2 for 16-bit audio
                ShortBuffer shortBuffer = byteBuffer.order(ByteOrder.BIG_ENDIAN).asShortBuffer();

                for (int i = 0; i < framesToRead; i++) {
                    float sample = buffer[i];
                    short pcmSample = (short) (sample * Short.MAX_VALUE);
                    shortBuffer.put(pcmSample);
                }

                byte[] byteArray = byteBuffer.array();
                bytesRead = byteArray.length;
                System.arraycopy(byteArray, 0, b, off, bytesRead);
            }

            bufferIndex += framesToRead / bufferLength;

            return bytesRead;
        }
    }

}
