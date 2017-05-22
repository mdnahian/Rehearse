from flask import Flask, request
from werkzeug.utils import secure_filename
from analyzer import execute
import string
import random
import os
import subprocess


app = Flask(__name__)
key = '4HkSMZVMyW83aMWgTqAV'

UPLOAD_FOLDER = 'static/audio/'
ALLOWED_EXTENSIONS = set(['wav', '3gp', 'mp3'])

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


def generate_random(n=8):
    return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(n))


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route('/')
def index():
    return 'running...'


@app.route('/api', methods=['GET', 'POST'])
def api():
    if request.method == 'POST':
        # api_key = request.form['api_key']
        api_key = key
        if api_key is key:
            if 'file' in request.files:
                file = request.files['file']
                if file and allowed_file(file.filename):
                    filelabel = generate_random()
                    filename = secure_filename(filelabel+'.mp4')
                    filepath = UPLOAD_FOLDER + filename
                    file.save(filepath)

                    newfilename = secure_filename(filelabel+'.wav')
                    newfilepath = UPLOAD_FOLDER + newfilename
                    command = "ffmpeg -i "+filepath+" -ab 160k -ac 2 -ar 44100 -vn "+newfilepath
                    subprocess.call(command, shell=True)

                    response = execute(newfilepath)

                    os.remove(filepath)
                    os.remove(newfilepath)

                    return response
                else:
                    return '{"status": "error", "message": "improper file format"}'
            else:
                return '{"status": "error", "message": "file not found"}'
        else:
            return '{"status": "error", "message": "api key invalid"}'


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=True)