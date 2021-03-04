import requests
import cv2
import RPi.GPIO as GPIO
import time
import threading
import numpy as np
from flask import jsonify

led_pin = 21
light_pin = 26
cams = []
cam_buffers = []
index = 0
MAX_BUFFER_SIZE = 15
collecting_frames = False
headers = {'content-type' : 'image/jpg'}
#SERVER_URL = "http://192.168.2.11:5000"
SERVER_URL = "http://72.141.213.167:40002"

def setup():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(light_pin, GPIO.IN)
    GPIO.setup(led_pin, GPIO.OUT)
    GPIO.output(led_pin, GPIO.LOW)
    
    global cams
    cams.append(cv2.VideoCapture(-1))
    cams.append(cv2.VideoCapture(2))
    cams.append(cv2.VideoCapture(4))

    global cam_buffers
    cam_buffers = []
    for i in range(3):
        cam_buffers.append([])

def read_light_sensor():
    setup()
    prev_value = None
    global collecting_frames

    try:
        print("Starting Light Sensors")
        while True:
            value = GPIO.input(light_pin)
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
    print("Collecting Frames")
    global collecting_frames
    global cam_buffers
    global cams

    index = 0

    while collecting_frames:
        for i in range(3):
            ret, frame = cams[i].read()

            if not ret:
                print("ERROR Bad Frame")
                break

            if len(cam_buffers[i]) < MAX_BUFFER_SIZE:
                cam_buffers[i].append(frame)

            else:
                cam_buffers[i][index] = frame
                
        index = index + 1
        index = index % MAX_BUFFER_SIZE
            
    for i in range(3):
        cams[i].release()
    cams = []

def avg_histogram_val(img):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    histogram = np.histogram(img, 256)[0]
    total = 0
    mean = 0

    for val in range(len(histogram)):
        total = total + histogram[val]
        mean = mean + (histogram[val] * val)

    mean = mean / total
    return mean

def send_to_server(img_arr):
    print("Sending images to server")
    s = requests.Session()

    for image in img_arr:
        _, img_encoded = cv2.imencode('.jpg', image)
        res = s.get(url=SERVER_URL+"/collectImage", data=img_encoded.tostring(), headers=headers)
        print(res.json())

    res = s.get(url=SERVER_URL+"/processImages")
    print(res.json())

def process_frames(name="pics/filteredFrame", server_active=True):
    global cam_buffers
    filtered_frames = []

    print("processing images")
    for camera_set in cam_buffers:
        max_val = 0
        best_img = None

        for pic in camera_set:
            histogram_val = avg_histogram_val(pic) 
            if histogram_val > max_val:
                best_img = pic
                max_val = histogram_val
        filtered_frames.append(best_img)
        cv2.imwrite(name+"{}.jpg".format(len(filtered_frames)), best_img)
        print(name+"{}.jpg".format(len(filtered_frames)))

    if(server_active):
        #Send filtered frames to server
        send_to_server(filtered_frames)

class camera_thread(threading.Thread):
    def __init__(self, name):
      threading.Thread.__init__(self)
      self.name = name

    def run(self):
       global collecting_frames
       collecting_frames = True
       collect_frames()


def main():
    while True:
        read_light_sensor()
        process_frames()
        print("Sleeping for 30 seconds..")
        time.sleep(30)
    print("finished")

def save_photos():
    global collecting_frames
    setup()
    set_name = input()
    camera = camera_thread("Camera Threading")
    camera.start()
    time.sleep(5)
    collecting_frames = False
    process_frames("testingData/"+set_name, False)
    print("saved pics")

if __name__ == "__main__":
   # first = cv2.imread("pics/1.jpg")
   # second = cv2.imread("pics/2.jpg")
    #third = cv2.imread("pics/3.jpg")

   # img_arr = [first, second, third]
    
   # send_to_server(img_arr)
    main()
    # save_photos()
