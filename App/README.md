# BizNotio
An app to connect investees to investor

### Features
* Realtime chatting 
* Peer-to-peer video calling
* Create startup posts
* Send payments to users
* Search Users with account types
* Maintain Account


## Getting Started


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

IntelliJ Ultimate 2020.2
Java 1.8 SDK

Dependencies are below:

build.gradle
```
    // Glide and picasso is used, we provided examples on how to use either library
    // to load images from a url
    def glide_version = "4.8.0"
    implementation "com.github.bumptech.glide:glide:$glide_version"
    annotationProcessor "com.github.bumptech.glide:compiler:$glide_version"

    // Circle image view crop dependency from another github
    implementation 'de.hdodenhof:circleimageview:3.1.0'

    // groupie recycler view helper dependency used for logical placement of items in recycler view
    def groupie_version = "2.8.1"
    implementation "com.xwray:groupie:$groupie_version"

    def picasso_version = "2.71828"
    implementation "com.squareup.picasso:picasso:$picasso_version"

    // Both ViewModels and LiveData are used for passing data between fragments and activities
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"

    // Twilio is used for video calling
    implementation "com.twilio:audioswitch:0.1.5"
    implementation "com.twilio:video-android:5.9.0"


    implementation "com.android.support.constraint:constraint-layout:1.1.3"
    implementation "com.koushikdutta.ion:ion:2.1.8"
    
```

### Installing

To install the application, check out the releases tab on the Github repo. This should be a simple APK 
that you can install directly to the phone. Since this APK will have our database connected, you won't 
need to deploy your own Firebase project or Node.js server to run the application. If you do want 
to build from source you will need to launch your own Firebase app and connect it to Android, you will
also have to launch your own server to handle token authorization for the Twilio programmable video SDK.


Steps to build from source:
```
Open IntelliJ and select new project from existing source 
```

![Quick-build-tutorial](source_biznotio_build_tutorial.gif)

On URL put this link
```
https://github.com/Sindhu-Parajuli/BizNotio2.git
```
![IntelliJ clone repo](https://i.ibb.co/ScYQhk2/image.png)

```
Wait for the gradle sync to finish. Press build on top portion of screen
```
Copy these two lines in `temp.txt`
```
TWILIO_ACCESS_TOKEN_SERVER=https://citrine-speckled-recess.glitch.me/
USE_TOKEN_SERVER=true
```
to `local.properties`

```
You will have a running app but if you want your own database follow the instructions below! 
```

### Set up the backend
[Create a Firebase project and link it to your clone](https://firebase.google.com/docs/android/setup)

[Follow this tutorial for Twilio programmable voice](https://github.com/twilio/video-quickstart-android)

[Launch the Node.js server for token authorization](https://github.com/TwilioDevEd/video-access-token-server-node)

That's all you need to do for the backend! 

### Back to IntelliJ
Run the app on your emulator from AVD Manager or use your phone
Instructions on testing the app on your phone can be found here:

[Android Developers Guide](https://developer.android.com/studio/run/device)


Now after you hook up the backend credentials to your clone
you will have a fully functioning Biznotio app! 😎✨🎉

## Demo
![Quick-demo-gif](source.gif)

## Built With

* [Figma](http://www.figma.com/) - Design and prototyping tool 
* [Kotlin](https://kotlinlang.org/) - Google's preferred language for Android app development
* [Jetpack](https://developer.android.com/jetpack/) - UI management and creation
* [Twilio](https://www.twilio.com/docs/video/) - Used for video calling feature
* [Firebase](https://firebase.google.com/) - Powerful, world-class infrastructure for mobile apps


## Contributing

If you would like to contribute, make a pull request to have it reviewed by one of us.

## Authors

* **Chowdhury, Lamia** - *Core Team Member* - [Profile](https://github.com/Lamiachowdhury)

* **Gupta, Sushant** - *Core Team Member* - [Profile](https://github.com/sushantcode)

* **Padilla, Jonathan** - *Core Team Member* - [Profile](https://github.com/jonathanpv)

* **Parajuli, Sindhu** - *Core Team Member* - [Profile](https://github.com/Sindhu-Parajuli)

Any contributors will be added to this list 😁

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* This project was for the Software Engineering course, CSE 3310, under [Professor Rodrigo Augusto Dos Santos](https://mentis.uta.edu/explore/profile/rodrigo-augusto-silva-dos-santos) 
* Thank you for visiting 🎉
