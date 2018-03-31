package com.models2you.model.job;

import android.text.TextUtils;

import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Amit on 8/24/2016.
 * Book Now API
 */
public class CallWithTwilioJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(CallWithTwilioJob.class);
    private final long calleePhone;
    private final long callerPhone;

    public CallWithTwilioJob(long calleePhone, long callerPhone) {
        super(new Params(Priority.HIGH).groupBy("GetModelCurrentLocationJob"));
        this.calleePhone = calleePhone;
        this.callerPhone = callerPhone;
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        log.verbose("onJobRun callee " + calleePhone + "caller" + callerPhone);
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        if (!TextUtils.isEmpty(token)) {
            Call<ResponseViews.TwilioCallResponseView> twilioCallResponseViewCall =
                    App.get().getApiFactory().getCommonApi().getTwilioCallResponse(token, id, calleePhone, callerPhone);
            log.verbose("getModelLocationResponseViewCall " + twilioCallResponseViewCall);

            Response<ResponseViews.TwilioCallResponseView> response = twilioCallResponseViewCall.execute();
            log.verbose("bookNowModelResponseCall BookNowModelResponse " + response);

            ResponseViews.TwilioCallResponseView twilioCallResponseView = response.body();
            App.get().getEventBus().postSticky(new Events.TwilioCallStatusEvent(twilioCallResponseView.isSuccess,
                    twilioCallResponseView.errorCode, twilioCallResponseView.errorMessage));
        } else {
            App.get().getEventBus().postSticky(new Events.TwilioCallStatusEvent(false, 0, ""));
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.TwilioCallStatusEvent(false, 0, ""));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) { return false; }
}
