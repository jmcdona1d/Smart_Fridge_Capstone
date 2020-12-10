from flask import Flask, make_response, jsonify, request
import numpy as np
import cv2

app = Flask(__name__)

@app.route("/")
def hello():
    return "<h1> Hello </h1>"

@app.route("/imageTest")
def process_image():
    r = request

    img = np.frombuffer(r.data, np.uint8)
    img = cv2.imdecode(img, cv2.IMREAD_COLOR)
    print(img)
    return make_response(jsonify({'result':'success', 'image shape':img.shape})), 200

if __name__ == "__main__":
    app.run('192.168.2.37')
