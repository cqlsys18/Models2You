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
 * Created by amitsingh on 4/26/2017.
 */

public class ChangePasswordJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(ChangePasswordJob.class);
    private String newPassword;

    public ChangePasswordJob(String newPassword) {
        super(new Params(Priority.HIGH).groupBy("change_password_job"));
        this.newPassword = newPassword;
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
        Call<ResponseViews.ChangePasswordResponse> changePasswordResponseCall =  App.get().getApiFactory().getCommonApi().changePassword(newPassword , id , token);
        Response<ResponseViews.ChangePasswordResponse> changePasswordResponseResponse = changePasswordResponseCall.execute();
        log.verbose("changePasswordResponse isSuccessful " + changePasswordResponseResponse.isSuccessful());
        if (changePasswordResponseResponse.isSuccessful()) {
            ResponseViews.ChangePasswordResponse response = changePasswordResponseResponse.body();
            log.verbose("LoginDataResponse " + response);
            if (response.isSuccess) {
                App.get().getEventBus().postSticky(new Events.ChangePasswordEventStickyResult(true));
            } else {
                App.get().getEventBus().postSticky(new Events.ChangePasswordEventStickyResult(false , response.errorMessage , response.errorCode));
            }
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.ChangePasswordEventStickyResult(false , ErrorView.CUSTOM_ERROR_MESSAGE, ErrorView.CUSTOM_ERROR_CODE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
