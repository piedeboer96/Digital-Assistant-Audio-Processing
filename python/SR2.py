import sys
import numpy as np
import torch
import librosa
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor

class SpeechRecognizer:
    def __init__(self, model_id):
        print('loading model')
        self.processor = Wav2Vec2Processor.from_pretrained(model_id)
        self.model = Wav2Vec2ForCTC.from_pretrained(model_id)

    def transcribe(self, audio_file):
        # Load the audio file
        speech_array, _ = librosa.load(audio_file, sr=16_000)
        inputs = self.processor(speech_array, sampling_rate=16_000, return_tensors="pt", padding=True)

        # Get the predicted logits from the model
        with torch.no_grad():
            logits = self.model(inputs.input_values, attention_mask=inputs.attention_mask).logits

        # Get the predicted IDs
        predicted_ids = torch.argmax(logits, dim=-1)

        # Decode the IDs to get the predicted sentence
        return self.processor.decode(predicted_ids[0])

def main():
    # Check if file path arguments are provided
    if len(sys.argv) < 2:
        print("Usage: python script.py [audio_file_path] ...")
        sys.exit(1)

    # Define the output file paths
    audio_file_paths = sys.argv[1:]

    # Define the speech recognizer
    recognizer = SpeechRecognizer("jonatasgrosman/wav2vec2-large-xlsr-53-english")

    for audio_file_path in audio_file_paths:
        # Transcribe the audio file
        print(f"Transcribing the audio file {audio_file_path}...")
        predicted_sentence = recognizer.transcribe(audio_file_path)

        # Print the transcribed sentence
        print("Transcription:", predicted_sentence)

if __name__ == "__main__":
    main()
