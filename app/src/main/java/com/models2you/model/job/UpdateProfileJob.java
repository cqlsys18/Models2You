package com.models2you.model.job;

import android.text.TextUtils;

import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by amitsingh on 10/3/2016.
 * Update profile Job
 */
public class UpdateProfileJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(ForgetPasswordJob.class);
    //private byte[] byteArrayImageBitmap;
    private String newImageUrl;
    private String name;
    private String rate;
    private String hairColor;
    private String favorites;
    private String eyeColor;
    private int footSelectedVal;
    private int inchSelectedVal;

    public UpdateProfileJob(String newImageUrl, String name, String rate, String hairColor, String favorites, String eyeColor, int footSelectedVal, int inchSelectedVal) {
        super(new Params(Priority.HIGH).groupBy("update_profile_job"));
        this.newImageUrl = newImageUrl;
        this.name = name;
        this.rate = rate;
        this.hairColor = hairColor;
        this.favorites = favorites;
        this.eyeColor = eyeColor;
        this.footSelectedVal = footSelectedVal;
        this.inchSelectedVal = inchSelectedVal;
    }

    public UpdateProfileJob(String name, String rate, String hairColor, String favorites, String eyeColor, int footSelectedVal, int inchSelectedVal) {
        super(new Params(Priority.HIGH).groupBy("update_profile_job"));
        this.name = name;
        this.rate = rate;
        this.hairColor = hairColor;
        this.favorites = favorites;
        this.eyeColor = eyeColor;
        this.footSelectedVal = footSelectedVal;
        this.inchSelectedVal = inchSelectedVal;
    }
    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        log.verbose("onRun");
        String appToken = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int userId = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("onRun appToken " + appToken + "userId " + userId);
        if (!TextUtils.isEmpty(appToken) && userId != 0) {
            // create Request Body
            Map<String, okhttp3.RequestBody> requestBodyMap = new HashMap<>();

            okhttp3.RequestBody id = okhttp3.RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
            okhttp3.RequestBody token = okhttp3.RequestBody.create(MediaType.parse("text/plain"), appToken);
            okhttp3.RequestBody name = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.name);
            okhttp3.RequestBody eyeColor = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.eyeColor);
            okhttp3.RequestBody rate = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.rate);
            okhttp3.RequestBody hairColor = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.hairColor);
            okhttp3.RequestBody favorites = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.favorites);
            if (this.newImageUrl != null) {
                okhttp3.RequestBody newImageUrl = okhttp3.RequestBody.create(MediaType.parse("text/plain"), this.newImageUrl);
                // added Map
                requestBodyMap.put("picture", newImageUrl);
            }
            okhttp3.RequestBody footSelectedVal = okhttp3.RequestBody.create(MediaType.parse("text/plain"),  String.valueOf(this.footSelectedVal));
            okhttp3.RequestBody inchSelectedVal = okhttp3.RequestBody.create(MediaType.parse("text/plain"),  String.valueOf(this.inchSelectedVal));

            requestBodyMap.put("token", token);
            requestBodyMap.put("id", id);
            requestBodyMap.put("name", name);
            requestBodyMap.put("eyecolor", eyeColor);
            requestBodyMap.put("haircolor", hairColor);
            requestBodyMap.put("rate", rate);
            requestBodyMap.put("height_foot", footSelectedVal);
            requestBodyMap.put("height_inch", inchSelectedVal);
            requestBodyMap.put("favorites", favorites);

            Call<ResponseViews.UpdateUserProfileResponse> updateUserProfileResponseCall = App.get().getApiFactory().getCommonApi().updateUserProfile(requestBodyMap);
            log.verbose("updateUserProfileResponseCall " + updateUserProfileResponseCall);
            Response<ResponseViews.UpdateUserProfileResponse> response = updateUserProfileResponseCall.execute();
            log.verbose("response " + response);
            ResponseViews.UpdateUserProfileResponse updateUserProfileResponse = response.body();
            log.verbose("onJobRun updateUserProfileResponse " + updateUserProfileResponse);
            if (updateUserProfileResponse.isSuccess) {
                App.get().getEventBus().postSticky(new Events.UpdateUserProfileEventSticky(true));
            } else {
                App.get().getEventBus().postSticky(new Events.UpdateUserProfileEventSticky(false, updateUserProfileResponse.errorCode, updateUserProfileResponse.errorMessage));
            }
        } else {
            // custom error creation when token is empty
            App.get().getEventBus().postSticky(new Events.UpdateUserProfileEventSticky(false, ErrorView.ERROR_CODE_NOT_VALID_TOKEN, ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN));
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.UpdateUserProfileEventSticky(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
