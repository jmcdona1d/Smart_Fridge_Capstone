import sys
import cv2 as cv
import numpy as np

import tensorflow as tf

# Load saved model
model_path = './models/first_model'
saved_model = tf.keras.models.load_model(model_path)

# Read image
img_path = './objectDetection/test_image.jpg'
img = cv.imread(img_path)



# resize image
new_height = 256
resize_factor = new_height/img.shape[0]
new_width = int(img.shape[1]*resize_factor)

resized = cv.resize(img, (new_width, new_height))

# create selective search segmentation object using default params
ss = cv.ximgproc.segmentation.createSelectiveSearchSegmentation()

ss.setBaseImage(resized)
ss.switchToSelectiveSearchQuality()

# Get region proposals in decreasing order of 'objectness'
rects = ss.process()

# get top region proposals
num_region_proposals = 200
rects = rects[:num_region_proposals]

# Crop and collect region proposals to create a batch of images
rects = rects/resize_factor # Get coordinates for original image size
rects = rects.astype(int)

batch = np.empty([num_region_proposals, 256, 256,3])
# Find a better way to do this
for i, rect in enumerate(rects):
    x,y,w,h = rect # bounding box of region proposal
    region = img[y:y+h, x:x+w] # crop image according to bounding box
    region = cv.resize(region, (256, 256))

    batch[i,:,:] = region

   

# For the time being not filtering out the 'poor' predictions.
# Need to decide on some score threshold
predictions = saved_model.predict(batch)

label_indices = np.argmax(predictions, axis=1)
print(label_indices)