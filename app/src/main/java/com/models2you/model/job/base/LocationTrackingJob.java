package com.models2you.model.job.base;

import android.text.TextUtils;

import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * LocationTrackingJob
 */
public class LocationTrackingJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(LocationTrackingJob.class);
    private double lat;
    private double lon;
    public LocationTrackingJob(double lat, double lon) {
        super(new Params(Priority.HIGH).groupBy("renew_token_expiry_job"));
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        log.verbose("onRun");
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN , "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("onJobRun token " + token + "id " + id);
        if (!TextUtils.isEmpty(token) && id != 0) {
            Call<ResponseViews.UpdateLocationResponseView> updateLocationResponseViewCall = App.get().getApiFactory().getCommonApi().
                    updateCurrentLocation(id, token, String.valueOf(lat), String.valueOf(lon)); //Server needed lat/lon in string.
            log.verbose("onJobRun updateLocationResponseViewCall " + updateLocationResponseViewCall);

            Response<ResponseViews.UpdateLocationResponseView> response = updateLocationResponseViewCall.execute();

            ResponseViews.UpdateLocationResponseView locationResponseView = response.body();
            if (locationResponseView.isSuccess) {
                App.get().getEventBus().postSticky(new Events.LocationUpdateEvent(true, 0, null, lat, lon));
            } else {
                if (locationResponseView.errorCode == ErrorView.TOKEN_EXPIRED_ERROR_CODE) {
                    App.get().stopLocationTracking(); // Stop location tracking if token gets expired.
                }
                App.get().getEventBus().postSticky(new Events.LocationUpdateEvent(false, response.body().errorCode, response.body().errorMessage, 0, 0));
            }
        } else {
            //If token empty, this case must not occur.
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.LocationUpdateEvent(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE, 0, 0));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
