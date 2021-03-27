# NHViewer

[![GitHub license](https://img.shields.io/github/license/ttdyce/NHentaiViewer?color=brightgreen)](https://github.com/ttdyce/NHentaiViewer/blob/master/LICENSE.md)
[![Build status](https://build.appcenter.ms/v0.1/apps/38433c11-f829-4f5d-8bea-418b4478d68a/branches/release/badge)](http://adfoc.us/5869871)

Simple third-party application for browsing nhentai.net.

> Just a little app for Android

## Recent changes: Visual Studio App Center

### Data collection

The app now uses [Visual Studio App Center](https://visualstudio.microsoft.com/zh-hant/app-center/) to provide in-app update, app analysis, and crashes report. 
To enable these features, some data will be collected from you (the user) and sent to App Center. You can find the details [here](https://docs.microsoft.com/en-us/appcenter/sdk/data-collected).

As it might be a concern to certain user, a new option is added in the settings page for this changes. 

### Better auto-update

Instead of using Firebase's remote config to handle update-checking manually, the app now uses [Visual Studio App Center](https://visualstudio.microsoft.com/zh-hant/app-center/) to handle it automatically (well, kind of...). <u>This feature will also be disabled along with disabling App Center Integration.</u> 



*[Known bugs](https://github.com/ttdyce/NHentaiViewer/projects/1)*😕

---

## Download

From VS App Center

- with ads: http://adfoc.us/5869871
- without ads: https://install.appcenter.ms/users/ttdyce/apps/nhviewer/distribution_groups/public

### Important note

- Download from VS App Center is recommended (to enable auto-update), rather than building locally.
- To support this project, you can...
  1. Sponsor this project on Patreon / GitHub! 

  2. Or... download from the first link 📺

  3. Or... star the repository 🌟

## Look and Feel - in demo mode( •̀ ω •́ )y

<img src="./screenshots/search.png" alt="Search result display demo" width="270"><img src="./screenshots/collection.png" alt="Collection display demo" width="270"><img src="./screenshots/setting.png" alt="Setting demo" width="270">

## Features

- Collection system
  - Add / remove comic from Favorite / Read later / History
  - Backup collection content to desktop (by scanning QRCode with [NHV-Backup, Java program](https://github.com/ttdyce/NHV-Backup))
  
- General
  - Vertical scrolling
  - Comic list sorting (by popularity / uploaded recently)
  - Search with specific language (Chinese / English / Japanese)

## V3 Overview

- Dark theme (good for your eyes😉, or, at least for me...)

- Better auto-update

  More new features (see #Roadmap)

## Roadmap

| Feature                                                      | Schedule        | Status  |
| ------------------------------------------------------------ | --------------- | ------- |
| In-app Proxy setting & **Exclusive Proxy Server for Patreon Sponsors** | mid-March ~ May | WIP     |
| Tag Filtering                                                | May ~ July      | Pending |
| Comic Descriptions on Appbar (e.g. authors/categories)       | July ~          | Pending |
| Cloud Sync (Either by nhentai account or by github/google account) | -               | Pending |
| and...                                                       | -               | -       |

---

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