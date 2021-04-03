#!/bin/bash

cd ~/git/yolov3-tf2
python3 detect.py \
    -image ./data/all1.jpg \
    -weights ./checkpoints/yolov3_train_403.tf \
    -tiny \
    -num_classes 6 \
    -classes ~/Documents/Smart_Fridge_Capstone/models/class.names
