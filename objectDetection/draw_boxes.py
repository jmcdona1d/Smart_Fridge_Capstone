import numpy as np
import cv2


def draw_boxes(image, annotation):

    height = image.shape[0]
    width = image.shape[1]
    num_items = annotation["items"]

    for i in range(num_items):

        xmin = int(annotation["xmin"][i]*width)
        ymin = int(annotation["ymin"][i]*height)
        xmax = int(annotation["xmax"][i]*width)
        ymax = int(annotation["ymax"][i]*height)

        label = annotation["classes_text"][i]

        cv2.rectangle(image, (xmin, ymin), (xmax, ymax), (0,255,0), 2)
        t_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_PLAIN, 1, 1)[0]
        cv2.rectangle(image, (xmin, ymin), (xmin+t_size[0]+3, ymin+t_size[1]+4), (0,255,0), -1)
        cv2.putText(image, label, (xmin, ymin+t_size[1]+4), cv2.FONT_HERSHEY_PLAIN, 1, [0,0,0], 1)
        return image
