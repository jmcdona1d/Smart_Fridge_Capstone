from pymongo import MongoClient

dbconnection = "mongodb+srv://{}:{}@capstone.o2r0q.mongodb.net/test?retryWrites=true&w=majority&ssl_cert_reqs=CERT_NONE".format("user", "8nA4UGa1SwF2CeJg")
database = MongoClient(dbconnection)

fridge_content  = database.FridgeContents.user0 #table of items for "user0" - possibly this could be dynamic if we implement support for multi-users
                  
def upload_to_fridge( data ):

    global fridge_content

    fridge_content.insert_one({
        "data" : data
        })





