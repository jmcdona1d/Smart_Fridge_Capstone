import sys
import cv2 as cv
import numpy as np

import tensorflow as tf

#hyperparameters
IMAGE_HEIGHT = 512
SCORE_THRESHOLD = .98
NUM_PROPOSALS = 2000

def filter_predictions( predictions, rects, threshold):
    keep = np.where(np.amax(predictions,1) > threshold)
    return predictions[keep], rects[keep]


def rcnnDetect(img):
    # Load saved model
    model_path = '../models/freiburg_mobilenetv2_model'
    saved_model = tf.keras.models.load_model(model_path)
    saved_model = tf.keras.Sequential([saved_model, 
                                            tf.keras.layers.Softmax()])


    # read class ids
    f = open('../objectDetection/classid.txt', 'r')
    class_ids = np.array(f.read().splitlines())

    # resize image
    resize_factor = IMAGE_HEIGHT/img.shape[0]
    new_width = int(img.shape[1]*resize_factor)

    resized = cv.resize(img, (new_width, IMAGE_HEIGHT))

    # create selective search segmentation object using default params            
    ss = cv.ximgproc.segmentation.createSelectiveSearchSegmentation()

    ss.setBaseImage(resized)
    ss.switchToSelectiveSearchFast()

    # Get region proposals in decreasing order of 'objectness'
    rects = ss.process()


    # Crop and collect region proposals to create a batch of images
    rects = rects/resize_factor # Get coordinates for original image size
    rects = rects.astype(int)

    pred_size = NUM_PROPOSALS if (rects.shape[0] > NUM_PROPOSALS) else rects.shape[0]

    predictions = np.empty([pred_size, class_ids.size])
    # Find a better way to do this
    for i, rect in enumerate(rects):
        if(i<NUM_PROPOSALS):
            x,y,w,h = rect # bounding box of region proposal
            if( w ==0 or h == 0):
                continue

            region = img[y:y+h, x:x+w] # crop image according to bounding box
            warped_region = cv.resize(region, (256, 256)) # warp region to input size of CNN
            region_batch = warped_region[np.newaxis, :] # input has to be a batch
            pred = saved_model.predict(region_batch)
            predictions[i] = pred[0]

    

    filtered_predictions, filtered_rects = filter_predictions(predictions, rects, SCORE_THRESHOLD)
    label_indices = np.argmax(filtered_predictions, axis=1)
    scores = np.max(filtered_predictions, axis=1)


    annotation = {
        "items": filtered_predictions.shape[0],
        "xmin": [filtered_rects[i,0].item() for i in range(filtered_predictions.shape[0])],
        "ymin": [filtered_rects[i,1].item() for i in range(filtered_predictions.shape[0])],
        "xmax": [(filtered_rects[i,0].item() + filtered_rects[i][2].item()) for i in range(filtered_predictions.shape[0])],
        "ymax": [(filtered_rects[i,1].item() + filtered_rects[i][3].item()) for i in range(filtered_predictions.shape[0])],
        "classes": [label_indices[i].item() for i in range(filtered_predictions.shape[0])],
        "classes_text": [class_ids[label_indices[i]] for i in range(filtered_predictions.shape[0])],
        "softmax": [scores[i].item() for i in range(filtered_predictions.shape[0])]
    }
    return annotation
