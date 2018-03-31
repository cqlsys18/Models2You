package com.models2you.model.job;

import android.content.SharedPreferences;

import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.fcm.NotificationHelper;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Amit on 9/1/2016.
 * Logout job call
 */
public class LogoutJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(LogoutJob.class);

    public LogoutJob() {
        super(new Params(Priority.HIGH).groupBy("logout_job"));
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        log.verbose("onJobRun");
        String deviceTokenToEmpty = ""; // set to device token empty - for gcm
        String deviceTypeToEmpty = ""; // set to deviceType Empty
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN , "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        Call<ResponseViews.LogoutResponse> logoutResponseCall = App.get().getApiFactory().getCommonApi().doLogout(token , id , deviceTokenToEmpty , deviceTypeToEmpty);
        log.verbose("onJobRun logoutResponseCall " + logoutResponseCall);
        Response<ResponseViews.LogoutResponse> response = logoutResponseCall.execute();
        ResponseViews.LogoutResponse logoutResponse = response.body();
        log.verbose("logoutResponse isSuccess " + logoutResponse.isSuccess);
        clearSharedPreferenceData();
        if (logoutResponse.isSuccess) {
            Utils.stopAutoLogoutService();
            NotificationHelper.clearNotifications();
            //GCMManager.getInstance().saveRegistrationStatus(false); // gcm token deleted
            App.get().getEventBus().postSticky(new Events.LogoutEventStatus(true));
            App.get().stopLocationTracking(); // stop location service when user logs out.
        } else {
            App.get().getEventBus().postSticky(new Events.LogoutEventStatus(false, response.body().errorCode, response.body().errorMessage));
        }
    }



    /**
     * clearSharedPreferenceData : method to clear shared preference data
     */
    private void clearSharedPreferenceData() {
        log.verbose("clearSharedPreferenceData logout Job ");
        String loggedInUserEmail = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.LOGGED_IN_USER_EMAIL , "");
        String loggedInUserPassword = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.LOGGED_IN_USER_PASSWORD , "");
        log.verbose("clearSharedPreferenceData loggedInUserEmail " + loggedInUserEmail + "loggedInUserPassword " + loggedInUserPassword );
        // clear whole preference except logged in user email and password
        App.get().getPreferenceEditor().clear().apply();
        // again save logged-in user email and password
        SharedPreferences.Editor editor = App.get().getPreferenceEditor();
        editor.putString(SharePreferenceKeyConstants.LOGGED_IN_USER_EMAIL , loggedInUserEmail);
        editor.putString(SharePreferenceKeyConstants.LOGGED_IN_USER_PASSWORD , loggedInUserPassword);
        editor.apply();
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.LogoutEventStatus(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        log.verbose("shouldReRunOnThrowable");
        return false;
    }
}
