#from cloudinary.uploader import upload
import cv2


def upload_image_path(path):
    result = upload(path, api_key=API_KEY, api_secret=API_SECRET, cloud_name=CLOUD_NAME)['url']
    return None

def upload_image(name, img):
    cv2.imwrite("pics/{}.jpg".format(name), img)
    result = upload_image_path("pics/{}.jpg".format(name))
    return None