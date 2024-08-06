package org.javapython;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JavaSpeechRecognizer {
    public static void main(String[] args) {
        PythonInterpreter interpreter = new PythonInterpreter();


        // Execute the Python script
        interpreter.execfile("/Users/piedeboer/Desktop/SpeechRecognition/git/SpeechRecognition/python/SpeechRecognizer.py");

        // Create an instance of the SpeechRecognizer class
        PyObject recognizer = interpreter.eval("SpeechRecognizer()");

        // Call the load_model method
        PyObject loadModelMethod = recognizer.invoke("load_model");

        // Check if the load_model method was executed successfully
        if (loadModelMethod == null) {
            System.out.println("Failed to load the model.");
        } else {
            System.out.println("Model loaded successfully.");
        }
    }
}
