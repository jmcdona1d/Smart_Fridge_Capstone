import cv2
print(cv2.__version__)
cam1 = cv2.VideoCapture(0)
cam2 = cv2.VideoCapture(1)
cam3 = cv2.VideoCapture(2)

cv2.namedWindow("cam1")

while True:

    ret1, frame1 = cam1.read()
    ret2, frame2 = cam2.read()
    ret3, frame3 = cam3.read()

    if not ret1 or not ret2 or not ret3:
        print("failed to grab frames")
        break
    cv2.imshow("cam1", frame)
    cv2.imshow('cam2', frame2)
    cv2.imshow('cam3', frame3)

    k = cv2.waitKey(1)
    if k%256 == 27:
        print("Escape hit, closing")
        break

cam1.release()
cam2.release()
cam3.release()
cv2.destroyAllWindows()
