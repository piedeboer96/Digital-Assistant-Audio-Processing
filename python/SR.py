import numpy as np
import torch
import librosa
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor
from py4j.java_gateway import JavaGateway, CallbackServerParameters

class SpeechRecognizer:
    def __init__(self, model_id):
        print('Loading model...')
        self.processor = Wav2Vec2Processor.from_pretrained(model_id)
        self.model = Wav2Vec2ForCTC.from_pretrained(model_id)
        print('Model loaded.')

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

if __name__ == "__main__":
    # Create a Py4J gateway server
    gateway = JavaGateway(callback_server_parameters=CallbackServerParameters())

    # Expose the SpeechRecognizer class as a Py4J gateway entry point
    gateway.entry_point.recognizer = SpeechRecognizer("jonatasgrosman/wav2vec2-large-xlsr-53-english")

    # Start the gateway server
    gateway.start_server()
