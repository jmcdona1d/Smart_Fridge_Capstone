import cv2
print(cv2.__version__)
cam = cv2.VideoCapture(0)

cv2.namedWindow("test")

while True:

    ret, frame = cam.read()
    if not ret:
        print("failed to grab frame")
        break
    cv2.imshow("test", frame)

    k = cv2.waitKey(1)
    if k%256 == 27:
        print("Escape hit, closing")
        break

cam.release()
cv2.destroyAllWindows()
