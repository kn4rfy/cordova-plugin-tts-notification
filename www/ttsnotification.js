/*
  Cordova Text-to-Speech Notification Plugin
  https://github.com/kn4rfy/cordova-plugin-tts-notification

  Created by FRANCIS KNARFY ELOPRE
  https://github.com/kn4rfy

  MIT License

  This plugin is using code from vilic's Cordova Text-to-Speech Plugin
  https://github.com/vilic/cordova-plugin-tts
*/

exports.speak = function (params, onfulfilled, onrejected) {
    var options = {};

    if (typeof params == 'string') {
        options.params = params;
    } else {
        options = params;
    }

    cordova
        .exec(function () {
            onfulfilled();
        }, function (reason) {
            onrejected(reason);
        }, 'TTSNotification', 'speak', [options]);
};
