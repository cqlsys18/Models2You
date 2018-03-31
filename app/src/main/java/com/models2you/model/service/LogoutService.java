package com.models2you.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.models2you.model.app.App;
import com.models2you.model.fcm.NotificationHelper;
import com.models2you.model.job.LogoutJob;
import com.models2you.model.util.LogFactory;


/**
 * Created by yogeshsoni on 06/10/16.
 * Logout service for AutoLogout App
 */

public class LogoutService extends Service {
    private static final LogFactory.Log log = LogFactory.getLog(LogoutService.class);
    private static final long AUTO_LOGOUT_TIME = 20000;

    @Override
    public void onCreate() {
        super.onCreate();
        log.verbose("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler uiHandler = new Handler();
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                App.get().getJobManager().addJob(new LogoutJob()); // logout after 10 sec when no action performed.
                NotificationHelper.clearNotifications();
            }
        }, AUTO_LOGOUT_TIME);
        // show notification for Renew or logout
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showCustomNotificationForLogout();
        log.verbose("onStartCommand");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
