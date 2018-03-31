package com.models2you.model.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Process;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.models2you.model.BuildConfig;
import com.models2you.model.R;
import com.models2you.model.fcm.GCMPreferenceManager;
import com.models2you.model.rest.ApiFactory;
import com.models2you.model.service.LocationTrackingService;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by yogeshsoni on 26/09/16.
 */

public class App extends Application {
    private static final LogFactory.Log log = LogFactory.getLog(App.class);

    private JobManager jobManager;
    private EventBus eventBus;
    private static App instance;
    private ApiFactory apiFactory;
    private SharedPreferences defaultAppSharedPreferences;
    private OkHttpClient simpleOkHttpClient;
    private GCMPreferenceManager gcmPreference;
    private Location lastActiveLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        checkAppReplacingState();
        instance = this;
        initCrashlytics();
        initCalligraphyFonts();
        initSimpleOkHttpClient();
        initDefaultPreferences();
        initApiFactory();
        initApp();
        initEventBus();
        initJobManager();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallback()); // activity life cycle callback
        initLocationTracking();
    }

    private void checkAppReplacingState() {
        if (getResources() == null) {
            Process.killProcess(Process.myPid());
        }
    }

    public void initLocationTracking() {
        String appToken = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int userId = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        if (!TextUtils.isEmpty(appToken) && userId != 0) {
            if (!isMyServiceRunning(LocationTrackingService.class)) {
                log.debug("startLocationTracking");
                startService(new Intent(this, LocationTrackingService.class));
            }
        } else {
            stopLocationTracking();
        }
    }

    public void stopLocationTracking() {
        log.debug("stopLocationTracking");
        stopService(new Intent(this, LocationTrackingService.class));
    }

    /**
     * initCrashlytics : method to initialize crashlytics with parameters
     */
    private void initCrashlytics() {
        Crashlytics kit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DISABLE_CRASHLYTICS).build())
                .build();
        Fabric.with(this, kit, new Crashlytics());

        Crashlytics.setString(Constants.CL_KEY_GIT_SHA, BuildConfig.GIT_SHA);
        Crashlytics.setString(Constants.CL_KEY_FLAVOR, BuildConfig.FLAVOR);
        Crashlytics.setString(Constants.CL_KEY_BUILD_TYPE, BuildConfig.BUILD_TYPE);
    }

    private void initCalligraphyFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    private void initEventBus() {
        this.eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .installDefaultEventBus();
    }


    /**
     * getPrefManager : method to return instance of GCM preference
     * class designed to store GCM messages locally
     * @return gcmPreferenceManager
     */
    public GCMPreferenceManager getGCMPreference() {
        if (gcmPreference == null) {
            gcmPreference = new GCMPreferenceManager(this);
        }
        return gcmPreference;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ApiFactory getApiFactory() {
        return apiFactory;
    }

    private void initApiFactory() {
        apiFactory = new ApiFactory();
    }

    protected void initApp() {
    }


    public final JobManager getJobManager() {

        return jobManager;
    }

    public static App get() {
        return instance;
    }

    private void initSimpleOkHttpClient() {
        simpleOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
    }

    private void initDefaultPreferences() {
        defaultAppSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public SharedPreferences getDefaultAppSharedPreferences() {
        return defaultAppSharedPreferences;
    }

    public SharedPreferences.Editor getPreferenceEditor() {
        return defaultAppSharedPreferences.edit();
    }

    private void initJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .minConsumerCount(1)
                .maxConsumerCount(3)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public OkHttpClient getSimpleOkHttpClient() {
        return simpleOkHttpClient;
    }

    /*To get my Running service*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * method  to set last active location of user
     * @param location : location instance
     */
    public void setLastActiveLocation(Location location) {
        this.lastActiveLocation = location;
    }

    /**
     * method to get last active location of user
     * @return last active location instance
     */
    public Location getLastActiveLocation() {
        return lastActiveLocation;
    }
}
