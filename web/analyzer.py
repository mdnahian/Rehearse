import speech_recognition as sr
import grammar_check
from pydub import AudioSegment
from pydub.silence import split_on_silence


filler_words = ['um', 'uh', 'right', 'like', 'gonna', 'ok', 'okay', 'you know', 'pretty much', 'sort of', 'basically', 'so']
sloppy_language = ['things', 'thing', 'this', 'that', 'they', 'those']

tool = grammar_check.LanguageTool('en-US')


# speech to text
def speech_to_text(audio_file):
    r = sr.Recognizer()
    with sr.AudioFile(audio_file) as source:
        audio = r.record(source)  # read the entire audio file
    r = sr.Recognizer()
    google_cloud_api = r'''{
          "type": "service_account",
          "project_id": "rehearse-168222",
          "private_key_id": "d6b6be2e1ad725973864bb4d3e1046a1f634d9bc",
          "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDHiHjUDQf59ItH\n6924FNenE5Z8pwspDzCbnrnbPGYGVw/BdwEjnoo5aqI9dmQ2/DC9vmgHQjmvEAHj\nN+PJKYePpkZxPixb9dPoFm55AnTcQQj/6vK3IVpHv1n1NOzQnozdN6yZrqhjbLKp\nROgzrfXDvzFySDnhxkgvb+ocaWRm3ipfcjj/jt8NcG54NfetNhxpiGklPxwVsHDT\nC+tS3aWsbWKqWEfxaItZYQgLSFVTMH3NjEQSniWxpijQ4eM+4kY4O7amVNPVrUtm\neJF0iRkjD2DPlxs/s+sTegI0eD2TX6+6zfDeEXfgsP7hZ+w6tZ2pX8BaiN88tMw2\nSxzFqNhrAgMBAAECggEAKlHbrr4EzxL654ozQHPLOXiCFnYne9Ho8Vi8+q7YxMd6\nZAIlj7R0hLEFMBwILVFX1SapUcDOEz9F5zdXbLcONX2Cu0oAhpChHTyEuc23k2+H\nxR/D0uuUBlujbMqI38aBAfpo9T+CH3RSgGxJA40F7zqB9kczX5t6e/U0pkoELXzZ\ngImH3+dCLdnQukIF9EDNUUHgsBx8LO2GmjnvJjUk52SOifFFaAGQfq+ZoAXNstFm\nLyU/Yt0eLIKCql+Q0Z4aSaBJqZR3323zyZqIvXt2t3yICtD0XZwMRTE9WUwgRE1u\nDrKkVSt8nIucLW0Dw52mFzi3+l04WmSLAvtORgLjgQKBgQDn4NOLQM21yLN9JAEZ\nLbweN33aUqNLhYrDRVFaqrf3fTK16Fkb3vCQETrHIO4LaWDsBLJcIptFIePHxVo5\nPUhOCaGgOKxk4esvNyJPPB3/60y8w7artHuBlsSwq9ltZtXAZvW+gZ3Y3ei+I3+q\ng5cb3grChp3Uml+65nLce1ZKEQKBgQDcSkJKQ/bT3Zn534moh8AY02QtZUMcox9Z\n3sE+XamQfGhQZ4jfI1ucGMdJ1hquPTiHiNfezO97rbrhSV7wohKRRXcue9Y6k0q0\n+5QY9yTQhpmdpjHtw/Abq3XXtkSdCi2Cqc2uDhIypKEMMlsolSG3joDW367gpl4a\nYcb9TQbeuwKBgDUlbbcUElIg/yqmju3jcqOGWn0oxa6wm/5nq97ZFDNQacgRyiK+\nNWr/1/X+ETFiZMmTxgGiGO5+dwO5sxU9e44PXREpDDJPKaV3wJBimgmnN75oKT0v\nr8Mi+E/6A2q00Dc6IgcZFEG5kvfJkSBW6tKDUPoG+rPY8ioXWao5RqzRAoGAWQfu\nKMO1I0WBsJpmsWVD/wSYjTxK1m0bA6Biq4kBOfL92dkuWTiaEsl06eONXvRBP43P\n6S0ccPjy+ZBEva5TIMFZfMDyATR8Ug8HeNuR1nE/7pY3waeaCCTQ+MCilHGDmTZ1\neS4Yrr0YlaxYaVt0iBIhAjbfH849ZB42ZTNXXncCgYAs/GhPhQ1/93k51t2kTyDb\nJBLkK2Q5U98DK8r7xUvhBgXoRjAOaFHhAzra+DaaP1chpYy8qy8jjSUjgx+FeXac\nlCKlKNMxQQ9JeocPoO2FzTPjWGqQr3TjaQ4/giAxq2yweH4NVRLLgfA7jDqfrfAb\no6TmtA0czP1ggFYYcZ4EpA==\n-----END PRIVATE KEY-----\n",
          "client_email": "speech-to-text@rehearse-168222.iam.gserviceaccount.com",
          "client_id": "114114010747321305637",
          "auth_uri": "https://accounts.google.com/o/oauth2/auth",
          "token_uri": "https://accounts.google.com/o/oauth2/token",
          "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
          "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/speech-to-text%40rehearse-168222.iam.gserviceaccount.com"
        }'''

    try:
        return r.recognize_google_cloud(audio, credentials_json=google_cloud_api)
    except sr.UnknownValueError:
        print("Google Cloud Speech could not understand audio")
    except sr.RequestError as e:
        print("Could not request results from Google Cloud Speech service; {0}".format(e))
    return None


# pauses
def count_pauses(audio_file):
    audio = AudioSegment.from_wav(audio_file)
    audio_chunks = split_on_silence(audio, min_silence_len=2000, silence_thresh=-16)
    return len(audio_chunks)


# grammatical errors
def correct_grammar(sentences):
    full = ''
    for sentence in sentences:
        matches = tool.check(sentence)
        full += grammar_check.correct(sentence, matches) + '. '
    return full


# filler words
def check_filler(arr):
    num_filler_words = 0
    for word in filler_words:
        num_filler_words += arr.count(word)
    return num_filler_words


# sloppy language
def check_language(arr):
    num_sloppy_language = 0
    for word in sloppy_language:
        num_sloppy_language += arr.count(word)
    return num_sloppy_language


def execute(audio_file):
    text = speech_to_text(audio_file)
    if text is not None:
        sentences = text.split('.')
        words = text.split(' ')
        num_sentences = len(sentences)
        num_words = len(words)
        num_filler_words = check_filler(text)
        num_sloppy_language = check_language(text)
        corrected = correct_grammar(sentences)
        num_pauses = count_pauses(audio_file)

        overall_grade = 'A'

        return '''
            {
                "status": "success",
                "overall_grade": "'''+overall_grade+'''",
                "sentences": "'''+str(num_sentences)+'''",
                "words": "'''+str(num_words)+'''",
                "filler_words": "'''+str(num_filler_words)+'''",
                "sloppy_language": "'''+str(num_sloppy_language)+'''",
                "pauses": "'''+str(num_pauses)+'''",
                "transcript": "'''+text+'''",
                "corrected": "'''+corrected+'''"
            }
        '''
    else:
        return '{"status": "error", "message": "Failed to transcribe audio"}'


# print execute('static/audio/NQDNIKBL.wav')