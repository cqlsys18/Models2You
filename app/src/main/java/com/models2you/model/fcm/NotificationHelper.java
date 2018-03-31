package com.models2you.model.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RemoteViews;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.util.Utils;
import com.models2you.model.widgets.CustomNotificationWidgetProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Amit on 9/28/2016.
 * NotificationHelper to show custom notification using GCM
 */
public class NotificationHelper {

    private static String TAG = NotificationHelper.class.getSimpleName();
    private Context mContext;

    private static final String onYesButtonClicked = "onYesButtonClicked";
    private static final String onNoButtonClicked = "onNoButtonClicked";

    public NotificationHelper() {
    }
 
    public NotificationHelper(Context mContext) {
        this.mContext = mContext;
    }
 
    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }
 
    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;
 
 
        // notification icon
        final int icon = R.mipmap.ic_launcher;
 
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
 
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
 
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/gcm_notification_sound");
 
        if (!TextUtils.isEmpty(imageUrl)) {
 
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
 
                Bitmap bitmap = getBitmapFromURL(imageUrl);
 
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }
 
 
    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
 
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
 
        if (GCMConfig.appendNotificationMessages) {
            // store the notification in shared pref first
            App.get().getGCMPreference().addNotification(message);
 
            // get the notifications from shared preferences
            String oldNotification =  App.get().getGCMPreference().getNotifications();
 
            List<String> messages = Arrays.asList(oldNotification.split("\\|"));
 
            for (int i = messages.size() - 1; i >= 0; i--) {
                inboxStyle.addLine(messages.get(i));
            }
        } else {
            inboxStyle.addLine(message);
        }
 
 
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
//                .setStyle(inboxStyle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();
 
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GCMConfig.NOTIFICATION_ID, notification);
    }

    private int getNotificationIcon(NotificationCompat.Builder mBuilder) {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        if (useWhiteIcon) {
            mBuilder.setColor(mContext.getResources().getColor(R.color.black));
        }
        return useWhiteIcon ? R.drawable.launcher_small_icon : R.mipmap.ic_launcher;
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();
 
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GCMConfig.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    public void showCustomNotificationForLogout() {
        // Using RemoteViews to bind custom layouts into Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.custom_notification_layout);

        Intent yesBtnIntent = new Intent(mContext, CustomNotificationWidgetProvider.class);
        yesBtnIntent.setAction(CustomNotificationWidgetProvider.onYesButtonClicked);
        PendingIntent yesBtnPendingIntent = PendingIntent.getBroadcast(mContext, 0, yesBtnIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btnNotificationYes, yesBtnPendingIntent);

        Intent noBtnIntent = new Intent(mContext, CustomNotificationWidgetProvider.class);
        noBtnIntent.setAction(CustomNotificationWidgetProvider.onNoButtonClicked);
        PendingIntent noBtnPendingIntent = PendingIntent.getBroadcast(mContext, 0, noBtnIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btnNotificationNo, noBtnPendingIntent );


        Intent notificationMessageIntent = new Intent(mContext, CustomNotificationWidgetProvider.class);
        notificationMessageIntent.setAction(CustomNotificationWidgetProvider.onNotificationMessageClicked);
        PendingIntent notificationMessagePendingIntent = PendingIntent.getBroadcast(mContext, 0, notificationMessageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notificationLayout, notificationMessagePendingIntent);

        // Open NotificationView Class on Notification Click
        // Send data to NotificationView Class
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/gcm_notification_sound");

        Notification notification = builder
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(Utils.getCurrentTimeInMillis())
                .setSmallIcon(getNotificationIcon(builder))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContent(remoteViews)
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT; // sticky notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GCMConfig.NOTIFICATION_ID, notification);

    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
 
    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + App.get().getApplicationContext().getPackageName() + "/raw/gcm_notification_sound");
            Ringtone r = RingtoneManager.getRingtone(App.get().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) App.get().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
 
    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}