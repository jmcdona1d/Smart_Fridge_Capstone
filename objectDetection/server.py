from flask import Flask, make_response, jsonify, request
from pymongo import MongoClient
import numpy as np
import cv2

app = Flask(__name__)

dbconnection = "mongodb+srv://{}:{}@capstone.o2r0q.mongodb.net/test?retryWrites=true&w=majority&ssl_cert_reqs=CERT_NONE".format("user", "8nA4UGa1SwF2CeJg")
database = MongoClient(dbconnection)

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
