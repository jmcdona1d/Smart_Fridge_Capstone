import numpy as np


def detect(image):

    #height, width = image.shape

    annotation = {
        "items": 3,
        "xmin": [0.13, 0.5, 0.0],
        "ymin": [0.73, 0.87, 0.34],
        "xmax": [0.44, 0.67, 0.41],
        "ymax": [0.92, 1.0, 0.69],
        "classes": [7, 25, 31],
        "classes_text": ["lemon", "strawberry", "wine"],
        "softmax": [0.98, 0.73, 0.99]
    }

    return annotation
