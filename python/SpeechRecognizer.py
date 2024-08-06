import torch
import librosa
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor

class SpeechRecognizer:
    def __init__(self):
        self.processor = None
        self.model = None

    def load_model(self):
        MODEL_ID = "jonatasgrosman/wav2vec2-large-xlsr-53-english"
        self.processor = Wav2Vec2Processor.from_pretrained(MODEL_ID)
        self.model = Wav2Vec2ForCTC.from_pretrained(MODEL_ID)

    def predict_output(self, audio_file_path):
        speech_array, sampling_rate = librosa.load(audio_file_path, sr=16_000)

        inputs = self.processor(speech_array, sampling_rate=16_000, return_tensors="pt", padding=True)

        with torch.no_grad():
            logits = self.model(inputs.input_values, attention_mask=inputs.attention_mask).logits

        predicted_ids = torch.argmax(logits, dim=-1)
        predicted_sentence = self.processor.decode(predicted_ids[0])

        print("Prediction:", predicted_sentence)
        return predicted_sentence


if __name__ == "__main__":
    recognizer = SpeechRecognizer()
    recognizer.load_model()

    audio_file = "/Users/piedeboer/Desktop/PROJECT/git/Project_2-2/out16.wav"
    recognizer.predict_output(audio_file)
