from cloudinary.uploader import upload
import cv2


def upload_image_path(path):
    result = upload(path, api_key="792227122254697", api_secret="JfCqgPYy-lb3LZVS_Spc5pO44rI", cloud_name="dcead5pak")
    return result

def upload_image(name, img):
    cv2.imwrite("pics/{}.jpg".format(name))
    result = upload_image_path("pics/{}.jpg".format(name))
    return result
