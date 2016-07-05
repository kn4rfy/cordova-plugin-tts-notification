# Cordova Text-to-Speech Notification Plugin

## Platforms

###### iOS 7 and UP
---
###### Android 4.0 and UP (API Level 15 to 23)
---

## Installation

```sh
cordova plugin add https://github.com/kn4rfy/cordova-plugin-tts-notification
```

## Usage

```javascript
document.addEventListener('deviceready', function () {

  TTSNotification.speak({title: 'Title', icon: 'icon', message: 'Message', language: 'en-US'}, function () {
    console.log('Speak success');
  }, function (reason) {
    console.log('Speak failed. error: ' + reason);
  });

}, false);
```

## Credits

This plugin is based from [Vilic](https://github.com/vilic)'s [Cordova Text-to-Speech Plugin](https://github.com/vilic/cordova-plugin-tts)

## Windows Phone

Windows version is not supported at this moment. It is available [here](https://github.com/vilic/cordova-plugin-tts) but you have to make the notification yourself.
