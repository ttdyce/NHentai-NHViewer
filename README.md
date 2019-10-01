# NHentaiViewer

[![Build Status](https://travis-ci.com/ttdyce/NHentaiViewer.svg?branch=master)](https://travis-ci.com/ttdyce/NHentaiViewer)

Third-party application for browsing [nhentai.net](https://nhentai.net). 

This application aims to provide different reading experience on android and to build further functions on top of the site. 

## Download

note: The signed apk is recommended. 

[Released apk](https://github.com/ttdyce/nhviewer/releases)

*Star the project may speed up patch release (¬‿¬ )*

*Version 2 will update the code, layout, and performance! (2019-09-19)*

## Screenshots

<img src="https://github.com/ttdyce/NHentaiViewer/raw/development/screenshots/favorite_list.png" alt="Comic display demo" width="280"><img src="https://github.com/ttdyce/NHentaiViewer/raw/development/screenshots/collection_list.png" alt="Collection display demo" width="280"><img src="https://github.com/ttdyce/NHentaiViewer/raw/development/screenshots/navigation_view.png" alt="Navigation" width="280">

## Features

- General
  - Displaying scroll-able comic list
  - Displaying vertically-scrolling comic content
  - Sorting comic list by popularity
  - Searching (With custom tags and language)
  - Page skipping

- Collection system
  - add / remove collection list
  - add / remove comic into collection list
  - backup collection content to desktop (using another Java program)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to run the app in debug mode.

By the way, the application is currently fetching the HTML page instead of using the JSON API as the API was down during my development.

## Deployment

Build and run the project inside Android Studio. 

## Built With

* [Android Studio](https://developer.android.com/studio)
* An Android device (on Android 8.0 Oreo)

*ps: I am using Android Studio 3.4, any version after 3.4 should be fine*

## Versioning

* [SemVer](http://semver.org/)

For the versions available, see the [tags on this repository](https://github.com/ttdyce/nhviewer/tags)

## Authors

* **ttdyce** - *Initial work* - [github](https://github.com/ttdyce)


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thanks for
  * Chinese translate from [neslxzhen](https://github.com/neslxzhen)
* Inspired by
  * [nhentai.net](https://nhentai.net)
  * [NHBooks](https://github.com/NHMoeDev/NHentai-android)
  * [EhViewer](https://github.com/seven332/EhViewer)
* Dependencies
  * [Gson](https://github.com/google/gson)
  * [jsoup](https://jsoup.org/download)
  * [Glide](http://bumptech.github.io/glide/doc/download-setup.html)
  * [Volley](https://developer.android.com/training/volley)

  and some Android support libraries
