import requests
import cv2
import RPi.GPIO as GPIO
import time
import threading
import numpy as np
from flask import jsonify

led_pin = 21
light_pin = 26
cam = cv2.VideoCapture(-1)
cam_buffer = []
index = 0
MAX_BUFFER_SIZE = 15
collecting_frames = False
headers = {'content-type' : 'image/jpg'}
SERVER_URL = "http://192.168.2.11:5000"

def setup():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(light_pin, GPIO.IN)
    GPIO.setup(led_pin, GPIO.OUT)
    GPIO.output(led_pin, GPIO.LOW)

def read_light_sensor():

    prev_value = None
    setup()
    global collecting_frames

    try:
        while True:
            value = GPIO.input(light_pin)
            print(value)
            if value != prev_value and prev_value != None:
                if(value == 0):
                    #start collecting frames thread
                    camera = camera_thread("collecting frames")
                    GPIO.output(led_pin, GPIO.HIGH)
                    camera.start()
                else:
                    #end frame collection and then main loop takes us to process frames
                    time.sleep(2)
                    GPIO.output(led_pin, GPIO.LOW)
                    collecting_frames = False
                    return
            prev_value = value
            time.sleep(1)
    finally:
        GPIO.cleanup()

def collect_frames():
    global collecting_frames
    global cam_buffer
    index = 0
    while collecting_frames:
         ret, frame = cam.read()

         if not ret:
            print("bad")
            break

         if len(cam_buffer) < MAX_BUFFER_SIZE:
            cam_buffer.append(frame)
            index = index+1
         else:
            cam_buffer[index] = frame
            index = index+1
        
         index = index % MAX_BUFFER_SIZE

def avg_histogram_val(img):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    histogram = np.histogram(img, 256)[0]
    total = 0
    mean = 0

    for val in range(len(histogram)):
        total = total + histogram[val]
        mean = mean + (histogram[val] * val)

    mean = mean / total
    print(mean)

def send_to_server(img_arr):
    s = requests.Session()

    for image in img_arr:
        _, img_encoded = cv2.imencode('.jpg', image)
        res = s.get(url=SERVER_URL+"/collectImage", data=img_encoded.tostring(), headers=headers)
        print(res.json())

    res = s.get(url=SERVER_URL+"/processImages")
    print(res.json())

def process_frames():
    global cam_buffer
    print("processing :", len(cam_buffer))
    for pic in range(len(cam_buffer)):
        avg_histogram_val(cam_buffer[pic]) 
        cv2.imwrite("pics/testingpic{}.jpg".format(pic), cam_buffer[pic])

class camera_thread(threading.Thread):
    def __init__(self, name):
      threading.Thread.__init__(self)
      self.name = name

    def run(self):
       global collecting_frames
       collecting_frames = True
       collect_frames()


def main():
    read_light_sensor()
    process_frames()
    print("finished")


if __name__ == "__main__":
    first = cv2.imread("pics/1.jpg")
    second = cv2.imread("pics/2.jpg")
    third = cv2.imread("pics/3.jpg")

    img_arr = [first, second, third]
    
    send_to_server(img_arr)
#    main()
