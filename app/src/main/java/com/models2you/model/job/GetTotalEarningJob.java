package com.models2you.model.job;

import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by amitsingh on 9/29/2016.
 * Get total earning job
 */
public class GetTotalEarningJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(GetTotalEarningJob.class);

    public GetTotalEarningJob() {
        super(new Params(Priority.HIGH).groupBy("current_booking_job"));
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("onRun token " +token + " id " + id);
        Call<ResponseViews.getTotalEarningResponse> getTotalEarningResponseCall = App.get().getApiFactory().getCommonApi().getTotalEarning(id, token);
        log.verbose("onJobRun getTotalEarningResponseCall " + getTotalEarningResponseCall);
        Response<ResponseViews.getTotalEarningResponse> response = getTotalEarningResponseCall.execute();
        ResponseViews.getTotalEarningResponse getTotalEarningResponse = response.body();
        if (getTotalEarningResponse.isSuccess) {
            App.get().getEventBus().postSticky(new Events.GetTotalEarningEventStatus(true , getTotalEarningResponse.totalEarning));
        } else {
            App.get().getEventBus().postSticky(new Events.GetTotalEarningEventStatus(false, response.body().errorCode, response.body().errorMessage));
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.GetTotalEarningEventStatus(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
