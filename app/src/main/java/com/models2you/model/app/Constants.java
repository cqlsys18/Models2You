package com.models2you.model.app;

/**
 * Created by yogeshsoni on 26/09/16.
 *
 */

public interface Constants {

//    username mmodels2you@gmail.com
//    password -   M1224play

    int MODEL_TYPE_MODEL = 1;

    //Dev
    //String BASE_URL = "http://staging.47billion.com"; // staging server
    String BASE_URL = "http://54.244.203.250"; // client staging server url
//    String BASE_URL = "http://52.11.251.134"; // client live server url
//    String BASE_URL = "http://54.213.253.84"; // client live clone server url


    // 1 denotes current user is model and 0 denotes current user is client
    int CURRENT_USER_MODEL = 1;

    //Crashlytics Keys
    String CL_KEY_GIT_SHA = "GIT_SHA";
    String CL_KEY_FLAVOR = "FLAVOUR";
    String CL_KEY_BUILD_TYPE = "BUILD";

    // date - time format
    String simpleDateFormat = "dd/M/yyyy";
    String simpleTimeFormat = "hh:mm a";
    String desiredFormattedDateToSend = "yyyy-MM-dd HH:mm:ss";

    String notAllowedDateFormat = "0000-00-00 00:00:00";

    // device type using in SignUp and Login
    String DEVICE_TYPE = "android";
    // gcm intent extras
    String INTENT_EXTRA_GCM_BOOKING_MESSAGE = "gcmMessage";
    String INTENT_EXTRA_NOTIFICATION_FROM_GCM = "notificationFromGCM";

    // reservation intent extra
    String INTENT_EXTRA_BOOKING_MODEL_POSITION = "bookingModelIntentExtraPosition";
    String INTENT_EXTRA_SELECTED_BOOKING_MODEL_LIST_ID = "bookingModel";
    String INTENT_EXTRA_BOOKING_MODEL_STATUS = "bookingModelStatus";

    // booking model list to send to Auto cancel service
    String INTENT_EXTRA_MODEL_LOCATTON_LAT = "modelLocationLat";
    String INTENT_EXTRA_MODEL_LOCATION_LNG = "modelLocationLng";

    //WebViewActivity
    String WEB_VIEW_URL_EXTRA = "web_view_url_extra";
    String WEB_VIEW_TITLE_EXTRA = "web_view_title_extra";
    String WEB_VIEW_NEED_ACTION_BACK = "web_view_need_back_button";
    String WEB_VIEW_LONG_PRESS_ENABLED = "web_view_long_press_enabled";

    //ManagePhoto Activity
    String INTENT_EXTRA_HIDE_ACTION_BUTTON = "hide_action_button";
}
