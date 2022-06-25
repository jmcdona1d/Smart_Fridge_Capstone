# Smart Fridge Capstone

This repository holds the code for my final year capstone project.

Our team developed a prototype for a Smart Fridge that would be able to detect what is placed inside of it and relay that information to the user's phone via a companion app. While we originally intended to create a demo inside of a working mini-fridge, due to COVID-19 we were unable to access the school's workshop facilities and we were instructed not to collaborate in-person. For these reasons, the final demo uses a storage container to emulate the inside of a fridge and an external light to activate the light sensor.

Our demo video can be viewed [here](https://www.youtube.com/watch?v=cgLgI4JdXpo) and it shows the flow of:
1. Opening the app to see an empty fridge
2. Showing the fridge now with items in it
3. Activating the light sensor which makes the cameras start capturing images
4. Deactivating the light sensor which sends the captured images to the server to be processed
5. The image classification model being run on the received images
6. Opening the app again to see the newly added items detected correctly

Technical Features which I contributed include:
- Created the light-sensing circuit and its controlling code
- Developed the logic for image capturing and selection based on brightness
- Developed external server to receieve the images and call classification model
- Setup cloud storage and database to be able to store images and model results
- Integrated mobile app with server to fetch and display dynamic results from the cloud

Technologies used: Python, Android Studio, Java, Raspberry Pi, Jetson Nano

I had a great time with this project and felt it was a great way to demonstrate the technical and non-technical engineering skills I developed over the course of my undergraduate career! If you would like to learn more you can read our report [here](https://drive.google.com/file/d/1NqtQVDjD98wBss_v4H2FcauZi-LmMDBg/view?usp=sharing).



