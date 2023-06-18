package org.javapython;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TranscriptionService {
    public String transcribe(String audioFilePath) {
        String transcribedText = "";

        try {
            String pythonScriptPath = "/Users/piedeboer/Desktop/SpeechRecognition/git/SpeechRecognition/python/SR.py";
            String[] cmd = {
                    "/usr/local/bin/python3",
                    pythonScriptPath,
                    audioFilePath
            };

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                transcribedText += line + "\n";
            }

            reader.close();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Handle any errors or exceptions
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return transcribedText;
    }



    public static void main(String[] args) {
        TranscriptionService service = new TranscriptionService();
        String audioFilePath = "/Users/piedeboer/Desktop/out16.wav";
        String transcribedText = service.transcribe(audioFilePath);
        System.out.println("Transcription: " + transcribedText);
    }
}
