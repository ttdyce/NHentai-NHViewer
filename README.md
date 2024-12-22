# NHViewer

[![GitHub license](https://img.shields.io/github/license/ttdyce/NHentaiViewer?color=brightgreen)](https://github.com/ttdyce/NHentaiViewer/blob/master/LICENSE.md)

Simple third-party application for browsing nhentai.net.

> Just a little app for Android

## Important Note: Visual Studio App Center is retiring soon!

written on 2024-12-23

Visual Studio App Center will be retired on March 31, 2025, which means this app won't receive auto update afterwards.  
To provide a wider support for both iOS and Android, I am working on a new solution on [ttdyce/nhviewer-universal](https://github.com/ttdyce/nhviewer-universal) as a replacement of this app. It's still under development but you can find the latest news there. 

---

## Download

[Download From VS App Center (retiring on March 31, 2025!)](https://install.appcenter.ms/users/ttdyce/apps/nhviewer-1/distribution_groups/public)

### Important note

- Download from VS App Center is recommended for auto-update
- To support this project, you can...
  1. Sponsor this project on GitHub

  3. Or... star the replacement app 🌟 [ttdyce/nhviewer-universal](https://github.com/ttdyce/nhviewer-universal)

## Look and Feel - in demo mode( •̀ ω •́ )y

<img src="./screenshots/search.png" alt="Search result display demo" width="270"><img src="./screenshots/collection.png" alt="Collection display demo" width="270"><img src="./screenshots/setting.png" alt="Setting demo" width="270">

## Features

- Collection system
  - Add / remove comic from Favorite / Read later / History
  - Backup collection content to desktop (by scanning QRCode with [NHV-Backup, Java program](https://github.com/ttdyce/NHV-Backup))
- General
  - Basic proxy
  - Vertical scrolling
  - Comic list sorting (by popularity / uploaded recently)
  - Search with specific language (Chinese / English / Japanese)

## V3 Overview

- Dark theme (good for your eyes😉, or, at least for me...)

- Better auto-update

  More new features (see #Roadmap)

### Version 2 overview

- [M-V-P](https://stackoverflow.com/questions/2056/what-are-mvp-and-mvc-and-what-is-the-difference)
- Retrieve data from [JSON API](https://github.com/NHMoeDev/NHentai-android/issues/27) (that page was removed, you may need to read the code/google it at this point...)
  - the closest document I could find is [this](https://hentaichan.pythonanywhere.com/projects/hentai/api-endpoints) by [@hentai-chan](https://github.com/hentai-chan)
- [Android Jetpack Components](https://developer.android.com/jetpack)

### Icon (Version 2) & Splash screen

I would like to include NHentai’s icon and slogan in this project since it is an application about their site.  
I have sent them an email to ask for permission but there is still no reply yet.  
Please contact me if there are any issue, thanks. 

---

## Getting Started

The application is using the [JSON API](https://github.com/NHMoeDev/NHentai-android/issues/27) (page was removed😥) and parse the response data into Java class from version 2.  

- the closest document I could find is [this](https://hentaichan.pythonanywhere.com/projects/hentai/api-endpoints) by [@hentai-chan](https://github.com/hentai-chan)


😣~~For more information about coding, see [the wiki](https://github.com/ttdyce/NHentaiViewer/wiki) (which is not yet ready '_' please come back later).~~

## Deployment

Build and run the project inside Android Studio.

## Built With

- [Android Studio](https://developer.android.com/studio)
  - Any version after Android Studio 3.5 should be fine
  - Run on an Android device (tested on Android 8.0 Oreo)

---

## Versioning

- [SemVer](http://semver.org/)

For the versions available, see the [tags on this repository](https://github.com/ttdyce/nhviewer/tags)

## Authors

- **ttdyce** - *Author and maintainer* - [github](https://github.com/ttdyce)

## Acknowledgments

- Thanks for
  - Simplified Chinese translate from [@History-exe](https://github.com/History-exe)
  - Traditional Chinese translate from [@neslxzhen](https://github.com/neslxzhen)
  - Beautiful badges displaying GitHub data from [Shields.io](https://github.com/badges/shields)
  
- Inspired by
  - [nhentai.net](https://nhentai.net)
  - [NHBooks](https://github.com/NHMoeDev/NHentai-android)
  - [EhViewer](https://github.com/seven332/EhViewer)
  
- Dependencies
  - Image blur: [glide-transformations](https://github.com/wasabeef/glide-transformations) from [@wasabeef](https://github.com/wasabeef)
  - [QRCodeReaderView](https://github.com/dlazaro66/QRCodeReaderView) from [@dlazaro66](https://github.com/dlazaro66)
  - [Gson](https://github.com/google/gson)
  - [jsoup](https://jsoup.org/download)
  - [Glide](http://bumptech.github.io/glide/doc/download-setup.html)
  - [Volley](https://developer.android.com/training/volley)
  - Android's libraries

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
