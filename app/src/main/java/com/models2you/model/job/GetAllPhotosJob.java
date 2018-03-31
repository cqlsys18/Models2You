package com.models2you.model.job;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by yogeshsoni on 30/09/16.
 */

public class GetAllPhotosJob extends Job {

    private static final LogFactory.Log log = LogFactory.getLog(GetCurrentBookingCountJob.class);

    public  GetAllPhotosJob() {
        super(new Params(Priority.HIGH).groupBy("get_all_photos_job"));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN , "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, R.drawable.user_profile_default);

        Call<ResponseViews.GetAllPhotosResponse>
                call = App.get().getApiFactory().getCommonApi()
                .getAllPhotos(token, id);
        Response<ResponseViews.GetAllPhotosResponse> response = call.execute();
        ResponseViews.GetAllPhotosResponse photosResponse = response.body();
        if(photosResponse.isSuccess){
            App.get().getEventBus().postSticky(
                    new Events.SetAllPhotosListEventStatus(true , photosResponse.photosList));
        }else{
            App.get().getEventBus().postSticky(
                    new Events.SetAllPhotosListEventStatus(false,
                            response.body().errorCode, response.body().errorMessage));
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.SetAllPhotosListEventStatus(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
