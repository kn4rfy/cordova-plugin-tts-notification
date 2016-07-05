/*
  Cordova Text-to-Speech Notification Plugin
  https://github.com/kn4rfy/cordova-plugin-tts-notification

  Created by FRANCIS KNARFY ELOPRE
  https://github.com/kn4rfy

  MIT License

  This plugin is using code from vilic's Cordova Text-to-Speech Plugin
  https://github.com/vilic/cordova-plugin-tts
*/

#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>
#import <AudioToolbox/AudioServices.h>

@interface TTSNotification : CDVPlugin <AVSpeechSynthesizerDelegate> {
  AVSpeechSynthesizer* synthesizer;
  NSString* lastCallbackId;
  NSString* callbackId;
}

- (void)speak:(CDVInvokedUrlCommand*)command;
@end
