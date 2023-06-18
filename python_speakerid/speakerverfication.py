import os
import wave
import time
import pickle
import pyaudio
import warnings
import numpy as np
from sklearn import preprocessing
from scipy.io.wavfile import read
import python_speech_features as mfcc
from sklearn.mixture import GaussianMixture 

warnings.filterwarnings("ignore")

"""
    TODO:
        - train for a few people of our group

"""

def calculate_delta(array):
   
    rows,cols = array.shape
    print(rows)
    print(cols)
    deltas = np.zeros((rows,20))
    N = 2
    for i in range(rows):
        index = []
        j = 1
        while j <= N:
            if i-j < 0:
              first =0
            else:
              first = i-j
            if i+j > rows-1:
                second = rows-1
            else:
                second = i+j 
            index.append((second,first))
            j+=1
        deltas[i] = ( array[index[0][0]]-array[index[0][1]] + (2 * (array[index[1][0]]-array[index[1][1]])) ) / 10
    return deltas

def extract_features(audio,rate):
       
    mfcc_feature = mfcc.mfcc(audio,rate, 0.025, 0.01,20,nfft = 1200, appendEnergy = True)    
    mfcc_feature = preprocessing.scale(mfcc_feature)
    print(mfcc_feature)
    delta = calculate_delta(mfcc_feature)
    combined = np.hstack((mfcc_feature,delta)) 
    return combined

def train_model():
    train_file = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/development_set_enroll.txt"
    source = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/development_set/"
    dest = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/trained_models/"

    file_paths = open(train_file, 'r')
    count = 1
    features = np.asarray(())

    for path in file_paths:
        path = path.strip()
        print(path)

        # Assuming you have the extract_features() function implemented correctly
        sr, audio = read(path)
        print(sr)
        vector = extract_features(audio, sr)

        if features.size == 0:
            features = vector
        else:
            features = np.vstack((features, vector))

        if count == 10:  # Change this value if you want to train the model with a different number of files
            gmm = GaussianMixture(n_components=6, max_iter=200, covariance_type='diag', n_init=3)
            gmm.fit(features)

            # Dumping the trained Gaussian model
            speaker_name = path.split("/")[-3]
            picklefile = speaker_name + ".gmm"
            pickle.dump(gmm, open(dest + picklefile, 'wb'))
            print('+ modeling completed for speaker:', speaker_name, " with data point =", features.shape)
            features = np.asarray(())
            count = 0
        count = count + 1

def test_model():
    source = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/testing_set"
    modelpath = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/trained_models/"
    test_file = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/testing_set_enroll.txt"
    
    with open(test_file, 'r') as file_paths:
        paths = file_paths.read().splitlines()

    gmm_files = [os.path.join(modelpath, fname) for fname in os.listdir(modelpath) if fname.endswith('.gmm')]

    # Load the Gaussian gender Models
    models = [pickle.load(open(fname, 'rb')) for fname in gmm_files]
    speakers = [fname.split("/")[-1].split(".gmm")[0] for fname in gmm_files]

    # Read the test directory and get the list of test audio files
    for path in paths:
        path = path.strip()
        print(path)
        sr, audio = read(os.path.join(source, path))
        vector = extract_features(audio, sr)

        log_likelihood = np.zeros(len(models))

        for i in range(len(models)):
            gmm = models[i]  # checking with each model one by one
            scores = np.array(gmm.score(vector))
            log_likelihood[i] = scores.sum()

        winner = np.argmax(log_likelihood)
        print("\tdetected as -", speakers[winner])
        time.sleep(1.0)


def identify_speaker(wav_clip):
    modelpath = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/trained_models/"
    
    gmm_files = [os.path.join(modelpath, fname) for fname in os.listdir(modelpath) if fname.endswith('.gmm')]

    # Load the Gaussian gender Models
    models = [pickle.load(open(fname, 'rb')) for fname in gmm_files]
    speakers = [fname.split("/")[-1].split(".gmm")[0] for fname in gmm_files]

    sr, audio = read(wav_clip)
    vector = extract_features(audio, sr)

    log_likelihood = np.zeros(len(models))

    for i in range(len(models)):
        gmm = models[i]  # checking with each model one by one
        scores = np.array(gmm.score(vector))
        log_likelihood[i] = scores.sum()

    # winner = np.argmax(log_likelihood)
    # predicted_speaker = speakers[winner]


    winner = np.argmax(log_likelihood)
    print("\tdetected as -", speakers[winner])
    
    return speakers[winner]


# print('Welcome. Training time!')
# train_model()
# print('Bye.')

# =================================== #
#   Testing Ground X

# start time
start_time = time.time()

identify_speaker("/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/peter.wav")

# print("Predicted Speaker:", predicted_speaker)

# end time
end_time = time.time()

execution_time = end_time - start_time
print("Execution Time:", execution_time, "seconds")