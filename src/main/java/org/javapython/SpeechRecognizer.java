package org.javapython;

import java.io.*;

public class SpeechRecognizer {

    public String transcribe(String audioFilePath) {
        String transcribedText = "";
        System.out.println("time to transcribe...");

        try {
            // Get the absolute path of the Python script
            String pythonScriptPath = "/Users/piedeboer/Desktop/SpeechRecognition/git/SpeechRecognition/python/SR.py";
            //String audioFilePath = "/Users/piedeboer/Desktop/SpeechRecognition/git/SpeechRecognition/out16khz.wav"
            String pythonExecutablePath = "/usr/local/bin/python3";

            // Create the Python script command with the absolute path and audio file path
            String[] cmd = {
                    pythonExecutablePath,
                    pythonScriptPath,
                    audioFilePath
            };

            // Create a new process builder
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);

            // Redirect error stream to avoid deadlocks and consume the output stream from the subprocess
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                transcribedText += line + "\n";
            }

            System.out.println("Transcription: " + transcribedText);

            reader.close();

            // Wait for the subprocess to finish
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return transcribedText;
    }
}
