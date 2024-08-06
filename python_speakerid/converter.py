import os
from pydub import AudioSegment

input_path = "/Users/piedeboer/Desktop/SpeakerIdentification/speakerid/singleshots"
output_path = "//Users/piedeboer/Desktop/SpeakerIdentification/speakerid/group1"

os.makedirs(output_path, exist_ok=True)

audio_files = os.listdir(input_path)

for file in audio_files:
    if file.endswith(".wav"):
        file_path = os.path.join(input_path, file)
        output_file = os.path.join(output_path, file)

        try:
            audio = AudioSegment.from_file(file_path)
            audio = audio.set_frame_rate(16000)  # Set the target sample rate to 16 kHz
            audio.export(output_file, format="wav")
            print(f"Converted: {file}")
        except Exception as e:
            print(f"Failed to convert: {file} - {str(e)}")
    else:
        print(f"Skipping: {file} (Not a WAV file)")

print("Conversion complete!")
