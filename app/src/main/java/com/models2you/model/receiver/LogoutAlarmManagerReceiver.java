package com.models2you.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.models2you.model.service.LogoutService;
import com.models2you.model.ui.activity.ReservationDetailActivity;
import com.models2you.model.util.LogFactory;

/**
 * Created by yogeshsoni on 06/10/16.
 */

public class LogoutAlarmManagerReceiver extends BroadcastReceiver {
    private static final LogFactory.Log log = LogFactory.getLog(LogoutAlarmManagerReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent callServiceIntent = new Intent(context, LogoutService.class);
        context.startService(callServiceIntent);
        log.error("LogoutAlarmManagerReceiver", "onReceive");
    }
}
