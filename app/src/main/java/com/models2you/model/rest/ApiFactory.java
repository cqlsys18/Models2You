package com.models2you.model.rest;

import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.rest.api.CommonApi;
import com.models2you.model.util.SharePreferenceKeyConstants;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yogeshsoni on 26/09/16.
 */

public class ApiFactory {

    private OkHttpClient client;
    private Retrofit retrofit;


    private CommonApi commonApi;

    public ApiFactory() {
        init();
    }

    private void init() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES);
        httpClient.addInterceptor(new Interceptor() {
                                      @Override
                                      public Response intercept(Chain chain) throws IOException {
                                          Request original = chain.request();
                                          Request request = original.newBuilder().header("content-type", "application/json")
                                                  .header("token", App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, ""))
                                                  .header("id", String.valueOf(App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0)))
                                                  .method(original.method(), original.body())
                                                  .build();
                                          return chain.proceed(request);
                                      }
                                  }
        );
        httpClient.addInterceptor(interceptor).build();
        client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        buildApi(retrofit);
    }

    public CommonApi getCommonApi(){
        return commonApi;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    //create APIs using restAdapter.create
    private void buildApi(Retrofit retrofit) {
        commonApi = retrofit.create(CommonApi.class);
    }
}
