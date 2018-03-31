package com.models2you.model.fcm;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Amit on 9/28/2016.
 * GCMPreferenceManager for shared preference about gcm
 */
public class GCMPreferenceManager {
    private String TAG = GCMPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;
 
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
 
    // Context
    Context _context;
 
    // Shared pref mode
    int PRIVATE_MODE = 0;
 
    // Sharedpref file name
    private static final String PREF_NAME = "models2You_gcm";
 
    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
 
    // Constructor
    public GCMPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    public void addNotification(String notification) {
 
        // get old notifications
        String oldNotifications = getNotifications();
 
        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
 
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }
 
    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }
 
    public void clear() {
        editor.clear();
        editor.commit();
    }
}