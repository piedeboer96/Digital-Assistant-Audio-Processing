 package org.audio;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;

 import javax.sound.sampled.AudioFileFormat;
 import javax.sound.sampled.AudioFormat;
 import javax.sound.sampled.AudioInputStream;
 import javax.sound.sampled.AudioSystem;
 import javax.sound.sampled.DataLine;
 import javax.sound.sampled.LineUnavailableException;
 import javax.sound.sampled.SourceDataLine;
 import javax.sound.sampled.TargetDataLine;

 /**
     CODE ADAPTED FROM
         @SOURCE:
         https://stackoverflow.com/questions/25798200/java-record-mic-to-byte-array-and-play-sound

  */

 public class AudioTest {

     public static void main(String[] args) {

         AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, true);
         TargetDataLine microphone;
         AudioInputStream audioInputStream;
         SourceDataLine sourceDataLine;
         try {
             microphone = AudioSystem.getTargetDataLine(format);

             DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
             microphone = (TargetDataLine) AudioSystem.getLine(info);
             microphone.open(format);

             ByteArrayOutputStream out = new ByteArrayOutputStream();
             int numBytesRead;
             int CHUNK_SIZE = 1024;
             byte[] data = new byte[microphone.getBufferSize() / 5];
             microphone.start();

             int bytesRead = 0;

             try {
                 while (bytesRead < 100000) {
                     numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                     bytesRead = bytesRead + numBytesRead;
                     System.out.println(bytesRead);
                     out.write(data, 0, numBytesRead);
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
             byte audioData[] = out.toByteArray();
             InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
             audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());

             // Save the audio data to a WAV file
             File file = new File("output.wav");
             AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);

             DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
             sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
             sourceDataLine.open(format);
             sourceDataLine.start();
             int cnt = 0;
             byte tempBuffer[] = new byte[10000];
             try {
                 while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                     if (cnt > 0) {
                         sourceDataLine.write(tempBuffer, 0, cnt);
                     }
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
             sourceDataLine.drain();
             sourceDataLine.close();
             microphone.close();
         } catch (LineUnavailableException | IOException e) {
             e.printStackTrace();
         }
     }
 }
