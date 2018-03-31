package com.models2you.model.fcm;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.rest.models.view.RequestBody;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SavePrefFcmToken;
import com.models2you.model.util.SharePreferenceKeyConstants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Amit on 9/28/2016.
 * MyFirebaseInstanceIdService for refreshing token
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final LogFactory.Log log = LogFactory.getLog(MyFirebaseInstanceIdService.class);

    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
 
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        log.verbose(TAG, "onTokenRefresh");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SavePrefFcmToken.setFcmDeviceToken(getApplicationContext(),"fcm_token",refreshedToken);
        sendRegistrationToServer(refreshedToken);
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
//        startService(intent);
    }

    private void sendRegistrationToServer(final String gcmToken) {
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        log.verbose("sendRegistrationToServer ownerId " + id , " deviceToken " + token , " gcmToken " + gcmToken);
        Call<ResponseViews.GCMRegistrationResponse> gcmRegistrationResponseCall = App.get().getApiFactory().getCommonApi().registerGcmToken(new RequestBody.GCMTokenRequestView(id, token, gcmToken , Constants.DEVICE_TYPE));
        try {
            Response<ResponseViews.GCMRegistrationResponse> response = gcmRegistrationResponseCall.execute();
            log.verbose("response  " + response);
            ResponseViews.GCMRegistrationResponse gcmRegistrationResponse = response.body();
            log.verbose("gcmRegistrationResponse isSuccess " + gcmRegistrationResponse.isSuccess);
            GCMManager.getInstance().saveRegistrationStatus(gcmRegistrationResponse.isSuccess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}