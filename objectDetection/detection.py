import os
import sys

import cv2
import numpy as np
import tensorflow as tf

sys.path.append('/home/elec490-10/git/yolov3-tf2/yolov3_tf2')
print(sys.path)

from models import YoloV3Tiny
from dataset import transform_images

BASE_DIR = '/home/elec490-10/Documents/Smart_Fridge_Capstone/models'
CLASS_NAMES = 'class.names'
WEIGHTS = 'yolov3-tiny.tf'
SIZE = 416
NUM_CLASSES = 6


def detect(image):

    physical_devices = tf.config.experimental.list_physical_devices('GPU')
    for physical_device in physical_devices:
        tf.config.experimental.set_memory_growth(physical_device, True)

    yolo = YoloV3Tiny(classes=NUM_CLASSES)

    yolo.load_weights(os.path.join(BASE_DIR, WEIGHTS)).expect_partial()

    class_names = [c.strip() for c in open(os.path.join(BASE_DIR, CLASS_NAMES)).readlines()]

    img = tf.convert_to_tensor(image)
    img = tf.expand_dims(img, 0)
    img = transform_images(img, SIZE)

    boxes, scores, classes, nums = yolo(img)

    boxes, scores, classes, nums = boxes[0].numpy(), scores[0].numpy(), classes[0].numpy(), nums[0]
    items = nums.numpy()
    if(items == 0):
        return None
    annotation = {
        "items": items.item(),
        "xmin": [boxes[i][0].item() for i in range(items)],
        "ymin": [boxes[i][1].item() for i in range(items)],
        "xmax": [boxes[i][2].item() for i in range(items)],
        "ymax": [boxes[i][3].item() for i in range(items)],
        "classes": [int(classes[i]) for i in range(items)],
        "classes_text": [class_names[int(classes[i])] for i in range(items)],
        "softmax": [scores[i].item() for i in range(items)]
    }
    return annotation
