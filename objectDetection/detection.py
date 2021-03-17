import os
import sys

import cv2
import numpy as np
import tensorflow as tf

sys.path.append('~/git/yolov3-tf2')

from yolov3_tf2.models import YoloV3Tiny
from yolov3_tf2.dataset import transform_images

BASE_DIR = '~/git/Smart_Fridge_Capstone/model'
CLASS_NAMES = 'class.names'
WEIGHTS = 'yolov3-tiny.tf'
SIZE = 416
NUM_CLASSES = 6


def detect(image):

    physical_devices = tf.config.experimental.list_physical_devices('GPU')
    for physical_device in physical_devices:
        tf.config.experimental.set_memory_growth(physical_device, True)

    yolo = Yolov3Tiny(classes=NUM_CLASSES)

    yolo.load_weights(os.path.join(BASE_DIR, WEIGHTS)).expect_partial()

    class_names = [c.strip() for c in open(os.path.join(BASE_DIR, CLASS_NAMES)).readlines()]

    img = tf.convert_to_tensor(image)
    img = transform_images(img, SIZE)

    boxes, scores, classes, nums = yolo(img)

    boxes, scores, classes, nums = boxes[0], scores[0], classes[0], nums[0]
    items = nums

    annotation = {
        "items": items,
        "xmin": [boxes[i][0] for i in range(items)],
        "ymin": [boxes[i][1] for i in range(items)],
        "xmax": [boxes[i][2] for i in range(items)],
        "ymax": [boxes[i][3] for i in range(items)],
        "classes": [int(classes[i]) for i in range(items)],
        "classes_text": [class_names[int(classes[i])] for i in range(items)],
        "softmax": [scores[i] for i in range(items)]
    }

    return annotation
