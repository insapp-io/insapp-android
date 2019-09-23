# Insapp

Android client of Insapp.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" 
      alt="Download from Google Play" 
      height="80">](https://play.google.com/store/apps/details?id=fr.insapp.insapp)

Related repositories : [iOS version](https://github.com/RobAddict/insapp-iOS), [backend](https://github.com/thomas-bouvier/insapp-server), [API](https://github.com/thomas-bouvier/insapp-go).

## What is Insapp?

Insapp is a project aiming at helping associations from INSA Rennes to communicate with students.
<img src="/screenshots/1.png?raw=true" width="200">
<img src="/screenshots/2.png?raw=true" width="200">
<img src="/screenshots/3.png?raw=true" width="200">
<img src="/screenshots/4.png?raw=true" width="200">
<img src="/screenshots/5.png?raw=true" width="200">
<img src="/screenshots/6.png?raw=true" width="200">
<img src="/screenshots/7.png?raw=true" width="200">

You can find us on [Facebook](https://www.facebook.com/insapp.crew/).

## Configuration

Use Android Studio to build the project.

Two build types (build variants) can be used:

- `release`: linked to the production API (`prod`) ;
- `debug` : linked to the development API (`dev`).

In order to receive push notifications under these two environments, two Firebase projects must be created. The attached `google-services.json` files should be at the following locations:

- `release`: `app/google-services.json` ;
- `debug` : `app/src/debug/google-services.json`.

## Notable open-source libraries

- [**Retrofit**](https://github.com/square/retrofit) for constructing the REST API
- [**Glide**](https://github.com/bumptech/glide) for loading images
- [**AppIntro**](https://github.com/AppIntro/AppIntro) for cool intro
- [**SparkButton**](https://github.com/varunest/SparkButton) for like buttons
- [**Android Vision API**](https://github.com/googlesamples/android-vision) for scanning barcodes
- [**AndroidX**](https://developer.android.com/jetpack/androidx)