package com.models2you.model.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AIM on 2/19/2018.
 */

public class SavePrefFcmToken {
    public static final String PREF_TOKEN = "M2UModel_Token";

    public static void setFcmDeviceToken(Context mContext, String key, String value) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFcmDeviceToken(Context mContext, String key,String def_value) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE);
        String stringvalue = preferences.getString(key, def_value);
        return stringvalue;
    }
}
