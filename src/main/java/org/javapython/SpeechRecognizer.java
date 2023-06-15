package org.javapython;

import java.io.*;

public class SpeechRecognizer {

    public String transcribe(String audioFilePath) {
        String transcribedText = "";

        try {
            // Define the Python script command
            String[] cmd = {
                "python",
                "/Users/lorispodevyn/Documents/JavaBook/SpeechRecognition/python/SR.py", // The path to the Python script
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
            reader.close();

            // Wait for the subprocess to finish
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return transcribedText;
    }
}
