/*
  Cordova Text-to-Speech Notification Plugin
  https://github.com/kn4rfy/cordova-plugin-tts-notification

  Created by FRANCIS KNARFY ELOPRE
  https://github.com/kn4rfy

  MIT License

  This plugin is using code from vilic's Cordova Text-to-Speech Plugin
  https://github.com/vilic/cordova-plugin-tts
*/

package co.ulimit.cordova.ttsnotification;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;


import java.util.HashMap;
import java.util.Locale;

public class TTSNotification extends CordovaPlugin implements OnInitListener {

  public static final String ERR_INVALID_OPTIONS = "ERR_INVALID_OPTIONS";
  public static final String ERR_NOT_INITIALIZED = "ERR_NOT_INITIALIZED";
  public static final String ERR_ERROR_INITIALIZING = "ERR_ERROR_INITIALIZING";
  public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

  protected static boolean isVisible = false;

  boolean ttsInitialized = false;
  TextToSpeech tts = null;

  @Override
  public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
    tts = new TextToSpeech(cordova.getActivity().getApplicationContext(), this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
      tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
          // do nothing
        }

        @Override
        public void onDone(String callbackId) {
          if (!callbackId.equals("")) {
            CallbackContext context = new CallbackContext(callbackId, webView);
            context.success();
          }
        }

        @Override
        public void onError(String callbackId) {
          if (!callbackId.equals("")) {
            CallbackContext context = new CallbackContext(callbackId, webView);
            context.error(ERR_UNKNOWN);
          }
        }
      });
    }
  }

  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (action.equals("speak")) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            speak(args, callbackContext);
          } catch (JSONException ignore) {}
        }
      });
      return true;
    }
    return false;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onInit(int status) {
    if (status != TextToSpeech.SUCCESS) {
      tts = null;
    } else {
      // warm up the tts engine with an empty string
      HashMap<String, String> ttsParams = new HashMap<String, String>();
      ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
      tts.setLanguage(new Locale("en", "US"));

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        tts.speak("", TextToSpeech.QUEUE_FLUSH, null, "");
      } else {
        tts.speak("", TextToSpeech.QUEUE_FLUSH, ttsParams);
      }

      ttsInitialized = true;
    }
  }

  @Override
  public void onResume(boolean isMultitasking)
  {
    isVisible = true;
  }

  @Override
  public void onPause(boolean isMultitasking)
  {
    isVisible = false;
  }

  int getResIdForDrawable(String clsName, String resPath) {
    String drawable = this.getBaseName(resPath);
    int resId = 0;

    try {
      Class<?> cls  = Class.forName(clsName + ".R$drawable");

      resId = (Integer) cls.getDeclaredField(drawable).get(Integer.class);
    } catch (Exception ignore) {}

    return resId;
  }

  private String getBaseName (String resPath) {
    String drawable = resPath;

    if (drawable.contains("/")) {
      drawable = drawable.substring(drawable.lastIndexOf('/') + 1);
    }

    if (resPath.contains(".")) {
      drawable = drawable.substring(0, drawable.lastIndexOf('.'));
    }

    return drawable;
  }

  @SuppressWarnings("deprecation")
  private void speak(final JSONArray args, final CallbackContext callbackContext) throws JSONException, NullPointerException {
    JSONObject params = args.getJSONObject(0);

    if (params == null) {
      callbackContext.error(ERR_INVALID_OPTIONS);
      return;
    }

    String title;
    String message;
    String language;

    if (params.isNull("title")) {
      callbackContext.error(ERR_INVALID_OPTIONS);
      return;
    } else {
      title = params.getString("title");
    }

    if (params.isNull("message")) {
      callbackContext.error(ERR_INVALID_OPTIONS);
      return;
    } else {
      message = params.getString("message");
    }

    if (params.isNull("language")) {
      language = "en-US";
    }
    else if (params.getString("language").equals("en")) {
      language = "en-US";
    } else {
      language = params.getString("language");
    }

    if (tts == null) {
      callbackContext.error(ERR_ERROR_INITIALIZING);
      return;
    }

    if (!ttsInitialized) {
      callbackContext.error(ERR_NOT_INITIALIZED);
      return;
    }

    final Intent emptyIntent = new Intent();
    Context context = this.cordova.getActivity().getApplicationContext();
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
    .setSmallIcon(this.getResIdForDrawable(context.getPackageName(), params.getString("icon")))
    .setContentTitle(title)
    .setContentText(message)
    .setContentIntent(pendingIntent);
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    HashMap<String, String> ttsParams = new HashMap<String, String>();
    ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

    String[] localeArgs = language.split("-");
    tts.setLanguage(new Locale(localeArgs[0], localeArgs[1]));

    if (isVisible) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, callbackContext.getCallbackId());
      } else {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, ttsParams);
      }
    }
    else{
      vibrate.vibrate(250);
      notificationManager.notify(0, mBuilder.build());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, callbackContext.getCallbackId());
      } else {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, ttsParams);
      }
    }
  }
}
