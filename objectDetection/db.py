from pymongo import MongoClient
from datetime import datetime
import image_store

dbconnection = "mongodb+srv://{}:{}@capstone.o2r0q.mongodb.net/test?retryWrites=true&w=majority&ssl_cert_reqs=CERT_NONE".format("user", "8nA4UGa1SwF2CeJg")
database = MongoClient(dbconnection)

fridge_content  = database.FridgeContents.user0  #table of items for "user0" - possibly this could be dynamic if we implement support for multi-users
                  
def upload_to_fridge( data ):

    global fridge_content
    
    fridge_content.drop()
    fridge_content.insert_one({
       "data" : data
       })

def merge_old_new( data, images ):

    result = {}
    class_names = {}
    result["items"] = []
    time_now = datetime.today()

    for data_set in data:
        if data_set is None:
            continue

        for i in range(data_set["items"]):

            if data_set["classes_text"][i] in class_names:
                continue

            item = {}
            item["xmin"] = data_set["xmin"][i]
            item["xmax"] = data_set["xmax"][i]
            item["ymin"] = data_set["ymin"][i]
            item["ymax"] = data_set["ymax"][i]
            item["class"] = data_set["classes"][i]
            item["class_text"] = data_set["classes_text"][i]
            item["softmax"] = data_set["softmax"][i]
            item["timestamp_added"] = time_now
            # Fix this with cropped images
            item["image_url"] = "https://res.cloudinary.com/dcead5pak/image/upload/v1615077687/e99jsqiiixmii5e9eeep.jpg"
            result["items"].append(item)
            class_names[data_set["classes_text"][i]] = True
    result["total_items"] = len(result["items"])
    #Upload images to cloud
    urls = []
    for img in images:
        urls.append(image_store.upload_image("upload", img)) 

    result["fridge_images"] = urls
    print(result)
    return result

