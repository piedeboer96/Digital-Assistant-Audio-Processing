# Multi-Modal Assistant: Speech Recognition and Speaker Identification

This repository contains the code for the speech recognition and speaker identification components of the multi-modal digital assistant developed for Project 2.2.

### Technologies Used

- **Java**: For basic audio processing tasks using TarsosDSP and JavaSound API.
- **Python**: For implementing speech recognition and speaker identification models.

### Models

- **Wav2Vec-2.0**: Utilized for high-accuracy transcription of raw audio. This model by Facebook/Meta uses self-supervised learning to achieve effective speech recognition.
- **Mozilla DeepSpeech**: Considered for its two-step process combining a deep neural network with an N-gram language model to convert audio into text.

### Speaker Identification

- **Mel-frequency Cepstral Coefficients (MFCC)**: Used for extracting speaker-specific acoustic features.
- **Gaussian Mixture Models (GMM)**: Employed for probabilistic modeling, enabling text-independent speaker identification by evaluating likelihood scores against these models.

### Authors

- Pie de Boer
- Loris Podevyn

### Date

- June 2023

