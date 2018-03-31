package com.models2you.model.job;


import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Amit on 8/16/2016.
 * Sign Up Job Call
 */
public class SignUpJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(SignUpJob.class);

    private String email;
    private String password;
    private String phone;
    private byte[] picturePath1, picturePath2, picturePath3, picturePath4;
    private File file;
    private String name;
    private String address;
    private String city;
    private String birthday;
    private String instagramId;
    private String facebookId;
    private String favorites;
    private String eyeColor;
    private String hairColor;
    private String zipCode;
    private String state;

    // hard- coded values
    private int rate = 50;
    private int heightFoot;
    private int heightInch;

    public SignUpJob(byte[] byteArrayImageBitmapOne, byte[] byteArrayImageBitmapTwo, byte[] byteArrayImageBitmapThree, byte[] byteArrayImageBitmapFour,File file, String email, String password, String phone, String name, String address, String city, String state, String birthday, String intagramId, String facebookId, String favorites, String eyeColor, String hairColor, int footSelectedVal, int inchSelectedVal, String zipCode) {
        super(new Params(Priority.HIGH).groupBy("sign_up_job"));
        this.picturePath1 = byteArrayImageBitmapOne;
        this.picturePath2 = byteArrayImageBitmapTwo;
        this.picturePath3 = byteArrayImageBitmapThree;
        this.picturePath4 = byteArrayImageBitmapFour;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.birthday = birthday;
        this.instagramId = intagramId;
        this.facebookId = facebookId;
        this.favorites = favorites;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.heightFoot = footSelectedVal;
        this.heightInch = inchSelectedVal;
        this.zipCode = zipCode;
        this.file = file;

    }

    public SignUpJob(byte[] byteArrayImageBitmapOne, byte[] byteArrayImageBitmapTwo, byte[] byteArrayImageBitmapThree, byte[] byteArrayImageBitmapFour, String email, String password, String phone, String name, String address, String city, String state, String birthday, String intagramId, String facebookId, String favorites, String eyeColor, String hairColor, int footSelectedVal, int inchSelectedVal, String zipCode) {
        super(new Params(Priority.HIGH).groupBy("sign_up_job"));
        this.picturePath1 = byteArrayImageBitmapOne;
        this.picturePath2 = byteArrayImageBitmapTwo;
        this.picturePath3 = byteArrayImageBitmapThree;
        this.picturePath4 = byteArrayImageBitmapFour;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.birthday = birthday;
        this.instagramId = intagramId;
        this.facebookId = facebookId;
        this.favorites = favorites;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.heightFoot = footSelectedVal;
        this.heightInch = inchSelectedVal;
        this.zipCode = zipCode;

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        // create Request Body
        RequestBody pictureFileRequestBodyOne = null, pictureFileRequestBodyTwo = null, pictureFileRequestBodyThree = null, pictureFileRequestBodyFour = null, pictureFileRequestBodyFive = null;
        if (picturePath1 != null) {
            pictureFileRequestBodyOne = RequestBody.create(MediaType.parse("image/png"), picturePath1);
        }
        if (picturePath2 != null) {
            pictureFileRequestBodyTwo = RequestBody.create(MediaType.parse("image/png"), picturePath2);
        }
        if (picturePath3 != null) {
            pictureFileRequestBodyThree = RequestBody.create(MediaType.parse("image/png"), picturePath3);
        }
        if (picturePath4 != null) {
            pictureFileRequestBodyFour = RequestBody.create(MediaType.parse("image/png"), picturePath4);
        }
        /*if (file != null) {
            pictureFileRequestBodyFive = RequestBody.create(MediaType.parse("text/pdf"), file);
        }*/
        // added Map
        Call<ResponseViews.SignUpResponse> signUpResponseCall = App.get().getApiFactory().getCommonApi().doSignUp(pictureFileRequestBodyOne, pictureFileRequestBodyTwo, pictureFileRequestBodyThree, pictureFileRequestBodyFour,
                 email, password, name, phone, address, state, city, instagramId, facebookId, favorites,
                eyeColor, hairColor, heightFoot, heightInch, rate, zipCode, birthday);
        log.verbose("signUpResponseCall " + signUpResponseCall);
        Response<ResponseViews.SignUpResponse> response = signUpResponseCall.execute();
        log.verbose("response " + response);
        ResponseViews.SignUpResponse signUpResponse = response.body();
        log.debug("signupJob response " + signUpResponse);
        if (signUpResponse.isSuccess) {
            App.get().getEventBus().postSticky(new Events.SignUpEventStickyResult(true));
        } else {
            App.get().getEventBus().postSticky(new Events.SignUpEventStickyResult(false, signUpResponse.errorMessage, signUpResponse.errorCode));
        }
    }


    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        App.get().getEventBus().postSticky(new Events.SignUpEventStickyResult(false, ErrorView.CUSTOM_ERROR_MESSAGE, ErrorView.CUSTOM_ERROR_CODE));
    }


    private boolean uploadFile(String url, String path, String contentType) throws IOException {
        File file = new File(path);
        Request request = new Request.Builder().header("Content-Type", contentType).url(url).put(RequestBody.create(MediaType.parse(contentType), file)).build();
        log.debug("uploadFile started.");

        OkHttpClient client = App.get().getSimpleOkHttpClient();

        okhttp3.Response response = client.newCall(request).execute();

        log.debug("uploadFile complete.");

        return response.isSuccessful();
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
