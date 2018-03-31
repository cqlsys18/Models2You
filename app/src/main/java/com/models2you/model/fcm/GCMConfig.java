package com.models2you.model.fcm;

/**
 * Created by Amit on 9/15/2016.
 * GCM config constants
 */
public class GCMConfig {
 
    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = false;
 
    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
 
    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
}