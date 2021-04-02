import sys
import cv2 as cv
import numpy as np

import tensorflow as tf

#hyperparameters
IMAGE_HEIGHT = 128
SCORE_THRESHOLD = 0.999
NUM_PROPOSALS = 2000

def filter_predictions( predictions, rects, threshold):
    keep = np.where(np.amax(predictions,1) > threshold)
    return predictions[keep], rects[keep]

def label_objects(img, label_indices, rects):
    font = cv.FONT_HERSHEY_SIMPLEX
    font_scale = 0.5
    rect_colour = (255,0,0)
    line_width = 3
    text_spacing = 30
    for i, label in enumerate(label_indices):
        x, y, w, h = rects[i]
        cv.rectangle(img, (x,y), (x+w, y+h), rect_colour, line_width)
        cv.putText(img, class_ids[label], (x+text_spacing, y+text_spacing), font, font_scale, (0,0,0), thickness= 5)
        cv.putText(img, class_ids[label], (x+text_spacing, y+text_spacing), font, font_scale, (255,255,255), thickness=1)

    cv.imshow('image', img)
    cv.waitKey(0)

# Load saved model
model_path = './models/freiburg_mobilenetv2_model'
saved_model = tf.keras.models.load_model(model_path)
saved_model = tf.keras.Sequential([saved_model, 
                                         tf.keras.layers.Softmax()])


# read class ids
f = open('./objectDetection/classid.txt', 'r')
class_ids = np.array(f.read().splitlines())

# Read image
img_path = './objectDetection/test_images/lightingfix1.jpg'
img = cv.imread(img_path)



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

batch = np.empty([rects.shape[0], 256, 256,3])

# Find a better way to do this
for i, rect in enumerate(rects):
    if(i<NUM_PROPOSALS):
        x,y,w,h = rect # bounding box of region proposal
        if( w ==0 or h == 0):
            continue

        region = img[y:y+h, x:x+w] # crop image according to bounding box
        batch[i,:,:] = cv.resize(region, (256, 256)) # warp region to input size of CNN

   
predictions = saved_model.predict(batch)

filtered_predictions, filtered_rects = filter_predictions(predictions, rects, SCORE_THRESHOLD)
label_indices = np.argmax(filtered_predictions, axis=1)

print(class_ids[label_indices])

# label_objects(img, label_indices, filtered_rects)