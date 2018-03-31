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
 * Created by amitsingh on 10/21/2016.
 * Renew token expiry job
 */
public class RenewTokenExpiryJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(RenewTokenExpiryJob.class);

    public RenewTokenExpiryJob() {
        super(new Params(Priority.HIGH).groupBy("renew_token_expiry_job"));
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN , "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("onJobRun token " + token + "id " + id);
        Call<ResponseViews.RenewTokenResponse> renewTokenResponseCall = App.get().getApiFactory().getCommonApi().renewTokenExpiry(id, token);
        log.verbose("onJobRun renewTokenResponseCall " + renewTokenResponseCall);
        Response<ResponseViews.RenewTokenResponse> response = renewTokenResponseCall.execute();
        ResponseViews.RenewTokenResponse renewTokenResponse = response.body();
        if (renewTokenResponse.isSuccess) {
            App.get().getEventBus().postSticky(new Events.RenewTokenExpiryEventStatus(true));
        } else {
            App.get().getEventBus().postSticky(new Events.RenewTokenExpiryEventStatus(false, response.body().errorCode, response.body().errorMessage));
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.RenewTokenExpiryEventStatus(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
