package com.models2you.model.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.fcm.NotificationHelper;
import com.models2you.model.job.LogoutJob;
import com.models2you.model.job.RenewTokenExpiryJob;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.Utils;

public class CustomNotificationWidgetProvider extends AppWidgetProvider {
    private static final LogFactory.Log log = LogFactory.getLog(CustomNotificationWidgetProvider.class);

    public static final String onYesButtonClicked = "onYesButtonClicked";
    public static final String onNoButtonClicked = "onNoButtonClicked";
    public static final String onNotificationMessageClicked = "onNotificationMessageClicked";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        log.verbose("onUpdate");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
        ComponentName thisWidget = new ComponentName(context, CustomNotificationWidgetProvider.class);
        remoteViews.setOnClickPendingIntent(R.id.btnNotificationYes, getPendingSelfIntent(context, onYesButtonClicked));
        remoteViews.setOnClickPendingIntent(R.id.btnNotificationNo, getPendingSelfIntent(context, onNoButtonClicked));
        remoteViews.setOnClickPendingIntent(R.id.notificationLayout, getPendingSelfIntent(context, onNotificationMessageClicked));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log.verbose("onReceive");
        Utils.stopAutoLogoutService(); // no need for running logout service
        if (onNoButtonClicked.equals(intent.getAction())) {
            log.verbose("onReceive onNoButtonClicked ");
            App.get().getJobManager().addJob(new LogoutJob());
            NotificationHelper.clearNotifications();
        } else {
            log.verbose("onReceive onYesButtonClicked ");
            App.get().getJobManager().addJob(new RenewTokenExpiryJob());
            NotificationHelper.clearNotifications();
        }
    }
}