import cv2
from detection import detect
from draw_boxes import draw_boxes

img = cv2.imread('/home/elec490-10/git/yolov3-tf2/data/all1.jpg')

annotation = detect(img)
print(annotation)
annotated_img = draw_boxes(img, annotation)

cv2.imwrite('img.jpg', img)
cv2.imwrite('output.jpg', annotated_img)
