from cloudinary.uploader import upload

result = upload("pics/testing.jpg", api_key=API_KEY, api_secret=API_SECRET, cloud_name=CLOUD_NAME)
print(result)
