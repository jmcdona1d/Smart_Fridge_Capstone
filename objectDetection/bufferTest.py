import cv2

cam = cv2.VideoCapture(0)
cam_buffer = []
index = 0
MAX_BUFFER_SIZE = 7

try:
    while True:

         ret, frame = cam.read()

         if not ret:
            print("bad")
            break;

         if len(cam_buffer) < MAX_BUFFER_SIZE:
            cam_buffer.append(frame)
            index = index+1
         else:
            cam_buffer[index] = frame
            index = index+1
        
         index = index % MAX_BUFFER_SIZE

except KeyboardInterrupt:
    pass

print("closed camera")
cam.release()

for i in range(0,MAX_BUFFER_SIZE):
    while True:
         cv2.imshow('frame {}'.format(i), cam_buffer[i])

         k = cv2.waitKey(1)
         if k%256 == 27:
             cv2.destroyAllWindows()
             break;
