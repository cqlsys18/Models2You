package com.models2you.model.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.models2you.model.service.LogoutService;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;

public class ActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static final LogFactory.Log log = LogFactory.getLog(ActivityLifecycleCallback.class);

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        boolean isAppInBackground = Utils.isAppIsInBackground(activity);
        log.verbose("onActivityStarted isAppInBackground " + isAppInBackground);
        if (!isAppInBackground && Utils.isServiceRunning(LogoutService.class)) {
            activity.stopService(new Intent(activity, LogoutService.class));
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        log.verbose("onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        log.verbose("onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        boolean isAppInBackground = Utils.isAppIsInBackground(activity);
        String appToken = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int userId = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("onActivityStopped isAppInBackground " + isAppInBackground + " appToken " + appToken + " userId " + userId);
        if (!TextUtils.isEmpty(appToken) && userId != 0 && isAppInBackground) {
            Utils.startAutoLogoutAlarmManager(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        log.verbose("onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        log.verbose("onActivityDestroyed");
    }
}