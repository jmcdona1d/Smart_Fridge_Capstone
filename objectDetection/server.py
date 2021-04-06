from flask import Flask, make_response, jsonify, request
import numpy as np

import sys

sys.path.append('/usr/local/lib/python3.8/site-packages')
import cv2
import detection_mock
import detection
import db
from datetime import datetime
import image_store

app = Flask(__name__)
image_buffer = []

@app.route("/")
def hello():
    return "<h1> Hello </h1>"

@app.route("/collectImage")
def collect_image():
    r = request
    img = np.frombuffer(r.data, np.uint8)
    img = cv2.imdecode(img, cv2.IMREAD_COLOR)
    image_buffer.append(img)
    return make_response(jsonify({'result':'success', 'image shape':img.shape})), 200

@app.route("/processImages")
def process_images():
    global image_buffer
    print("{} images Received".format(len(image_buffer)))

    #Call to image processing methods here
    
    res1 = detection.detect(image_buffer[0])
    res2 = detection.detect(image_buffer[1])
    res3 =  detection.detect(image_buffer[2])
    res = db.merge_old_new([res1, res2, res3], image_buffer)
    db.upload_to_fridge(res)
    image_buffer = [] #reset buffer for next call - we might actually end up wanting to save images to db app before this

    return make_response(jsonify({'result':'success'})), 200

@app.route("/uploadTest")
def db_test():
    res = detection_mock.detect(None)   
    img_url = image_store.upload_image_path("testingData/all2.jpg")
    res['timestamp'] = datetime.today()
    res['image_url'] = img_url
    db.upload_to_fridge(res)
    return "Image Uploaded"


@app.route("/contents", methods=["GET", "PUT"])
def contents():
    if request.method == "GET":
        res = db.fetch_fridge_contents()
        return res

    elif request.method == "PUT":
        db.upload_to_fridge(request)
        return "image uploaded succesfully"

@app.route("/appUpdate/<userInput>", methods=['POST'])
def recieve_input(userInput):
    print (userInput.json)
    db.upload_to_fridge(userInput.json)
    return userInput

if __name__ == "__main__":
#    app.run('192.168.2.37')
    app.run(host='0.0.0.0', port=40002)
