package com.models2you.model.fcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.event.Events;
import com.models2you.model.model.BookingModel;
import com.models2you.model.ui.activity.MainActivity;
import com.models2you.model.ui.activity.ReservationDetailActivity;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Amit on 9/28/2016.
 * MyFirebaseMessagingService listen incoming push notification
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final LogFactory.Log log = LogFactory.getLog(MyFirebaseMessagingService.class);

    // Constants
    private static final String KEY_TEAM = "team";
    private static final String KEY_ID = "id";
    private static final String KEY_APS = "aps";
    private static final String KEY_ALERT = "alert";
    private static final String KEY_BADGE = "badge";
    private static final String KEY_STATUS = "status";
    private static final String KEY_NOTIFY_STATUS = "notify_status";

    private NotificationHelper notificationHelper;


    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
//        Map data = message.getData();
        Log.e("firebase",from);
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        parseDataFromBundle(bundle);
    }

    /**
     * parseDataFromBundle : method to parse data from bundle
     *
     * @param bundle : bundle
     */
    private void parseDataFromBundle(Bundle bundle) {
        int bookingId = 0, status = 0, notifyStatus = 0;
        JSONObject json = null;
        String message = null;
        String key = bundle.getString(KEY_TEAM);
        try {
            if (!TextUtils.isEmpty(key)) {
                JSONObject jsonObject = new JSONObject(key);
                if (jsonObject.has(KEY_ID)) {
                    bookingId = jsonObject.getInt(KEY_ID);
                    log.verbose("bookingId " + bookingId);
                }
                if (jsonObject.has(KEY_STATUS)) {
                    status = jsonObject.getInt(KEY_STATUS);
                    log.verbose("status " + status);
                }
                if (jsonObject.has(KEY_NOTIFY_STATUS)) {
                    notifyStatus = jsonObject.getInt(KEY_NOTIFY_STATUS);
                    log.verbose("notifyStatus " + notifyStatus);
                }
                if (jsonObject.has(KEY_APS)) {
                    json = jsonObject.getJSONObject(KEY_APS);
                    log.verbose("json " + json);
                }
                if (json != null && json.has(KEY_ALERT)) {
                    message = json.getString(KEY_ALERT);
                    log.verbose("message " + message);
                }
                if (json != null && json.has(KEY_BADGE)) {
                    int badge = json.getInt(KEY_BADGE);
                    log.verbose("badge " + badge);
                }
                if (bookingId != 0 && !TextUtils.isEmpty(message) && !Utils.isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(getApplicationContext(), ReservationDetailActivity.class);
                    pushNotification.putExtra(Constants.INTENT_EXTRA_GCM_BOOKING_MESSAGE, message);
                    pushNotification.putExtra(Constants.INTENT_EXTRA_NOTIFICATION_FROM_GCM, true);
                    pushNotification.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, bookingId);
                    pushNotification.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_STATUS, status);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    long currentTimeMillis = Utils.getCurrentTimeInMillis();
                    String timeMills = Utils.convertTimeMillisToDateFormat(currentTimeMillis, Constants.desiredFormattedDateToSend);
                    if (status == BookingModel.BOOKING_STATUS.BOOKED.ordinal()) {
                        App.get().getEventBus().postSticky(new Events.GCMEventStickyResult(bookingId, message, status));
                    } else {
                        showNotificationMessage(getApplicationContext(), message, message, timeMills, pushNotification);
                        App.get().getEventBus().postSticky(new Events.ShowAlertMessageOnNotificationEventStatus(message));
                    }
                    App.get().getEventBus().postSticky(new Events.UpdateReservationScreenEventStatus(bookingId, status));
                } else if (bookingId != 0 && !TextUtils.isEmpty(message)) {
                    long currentTimeMillis = Utils.getCurrentTimeInMillis();
                    String timeMills = Utils.convertTimeMillisToDateFormat(currentTimeMillis, Constants.desiredFormattedDateToSend);



                    if (status == BookingModel.BOOKING_STATUS.BOOKED.ordinal()) {
                        Intent intent = new Intent(Utils.NOTIFICATION_MESSAGE);
                        intent.putExtra("message", message);
                        intent.putExtra("bookingId", bookingId);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra(Constants.INTENT_EXTRA_GCM_BOOKING_MESSAGE, message);
                    resultIntent.putExtra(Constants.INTENT_EXTRA_NOTIFICATION_FROM_GCM, true);
                    resultIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, bookingId);
//                    long currentTimeMillis = Utils.getCurrentTimeInMillis();
//                    String timeMills = Utils.convertTimeMillisToDateFormat(currentTimeMillis, Constants.desiredFormattedDateToSend);
                    showNotificationMessage(getApplicationContext(), message, message, timeMills, resultIntent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationHelper = new NotificationHelper(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationHelper = new NotificationHelper(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}