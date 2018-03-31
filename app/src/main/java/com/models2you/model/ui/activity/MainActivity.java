package com.models2you.model.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.event.Events;
import com.models2you.model.job.GetCurrentBookingCountJob;
import com.models2you.model.job.GetTotalEarningJob;
import com.models2you.model.job.SendTokenJob;
import com.models2you.model.job.SetAvailabilityStatusJob;
import com.models2you.model.model.BookingModel;
import com.models2you.model.service.LocationTrackingService;
import com.models2you.model.ui.base.BaseAppCompatActivity;
import com.models2you.model.util.DialogHelper;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SavePrefFcmToken;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.models2you.model.ui.activity.ReservationDetailActivity.REQUEST_CHECK_SETTINGS;

public class MainActivity extends BaseAppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final LogFactory.Log log = LogFactory.getLog(MainActivity.class);

    @BindView(R.id.currentBookingCount)
    TextView currentBookingCount;
    @BindView(R.id.totalEarningAmount)
    TextView totalEarningAmount;
    @BindView(R.id.switchButtonMsg)
    TextView switchButtonMsg;
    @BindView(R.id.exitRelativeLay)
    RelativeLayout exitRelativeLay;
    @BindView(R.id.currentBookingRelativeLay)
    RelativeLayout currentBookingRelativeLay;
    @BindView(R.id.switchButton)
    Switch onlineOfflineSwitch;
    @BindView(R.id.mainScreenSwipeToRefresh)
    SwipeRefreshLayout mainScreenSwipeToRefresh;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    private boolean isSilentLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buildGoogleApiClient();
        // check GCM is registered or not
//        GCMManager.getInstance().sendGCMTokenToServer();

        Log.e("fcm", "token " + SavePrefFcmToken.getFcmDeviceToken(MainActivity.this, "fcm_token", ""));
        sendRegistrationToken(SavePrefFcmToken.getFcmDeviceToken(MainActivity.this, "fcm_token", ""));
        getCurrentBookingCountJob();
        getTotalEarningJob();
        getIntentExtras();
        initLocationTracking();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Utils.NOTIFICATION_MESSAGE));
    }

    private void getIntentExtras() {
        if (getIntent().getBooleanExtra(Constants.INTENT_EXTRA_NOTIFICATION_FROM_GCM, false)) {
            Intent toReservationDetailIntent = new Intent(this, ReservationDetailActivity.class);
            toReservationDetailIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, getIntent().getIntExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, 0));
            startActivity(toReservationDetailIntent);
        } else {
            SharedPreferences sp = App.get().getDefaultAppSharedPreferences();
            int position = sp.getInt(SharePreferenceKeyConstants.PENDING_ACCEPT, -1);
            if (position != -1) {
                Intent toReservationDetailIntent = new Intent(this, ReservationDetailActivity.class);
                toReservationDetailIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, position);
                startActivity(toReservationDetailIntent);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(20 * 1000); // 1 sec

//        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            showLongSnackBar(getResources().getString(R.string.error_check_internet));
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (enableGPSIfNot()) {
                    Log.e("here", "enable");
                    mGoogleApiClient.connect();
                } else {
                    Log.e("here", "not enable");
//                    settingRequestForGPS();
                    showGPSDisabledAlertToUser();
                }
            }
        }, 100);


        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    private boolean enableGPSIfNot() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled) {

            return false;
        } else {
            return true;
        }
    }

    @OnClick(R.id.currentBookingRelativeLay)
    public void onCurrentBookingBlueIconClicked(View view) {
        onOptionsMenuCurrentReservationClicked(view);
    }

    @OnClick(R.id.exitRelativeLay)
    public void onExitIconClicked(View view) {
        onOptionsMenuLogoutClicked(view);
    }

    /**
     * getTotalEarningJob : method to get Total Earning using Job
     */
    private void getTotalEarningJob() {
        App.get().getJobManager().addJob(new GetTotalEarningJob());
    }

    /**
     * getCurrentBookingCountJob : method to get Current Booking count
     * using job
     */
    private void getCurrentBookingCountJob() {
        App.get().getJobManager().addJob(new GetCurrentBookingCountJob());
    }

    private void init() {
        imgUserProfileBtn.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.GONE);

        // check availability and set check status
        int isAvailable = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_AVAILABILITY, 0);
        if (isAvailable == 1) {
            onlineOfflineSwitch.setChecked(true);
            switchButtonMsg.setText(getResources().getString(R.string.model_online_msg));
        } else {
            onlineOfflineSwitch.setChecked(false);
            switchButtonMsg.setText(getResources().getString(R.string.model_offline_msg));
        }
        // onlineOfflineSwitch.setChecked(false);

        if (!onlineOfflineSwitch.isChecked()) {
            App.get().getJobManager().addJob(new SetAvailabilityStatusJob(true, BookingModel.AVAILABILITY_STATUS.ONLINE.ordinal()));
        }
        /* listener to listen switch statement */
        onlineOfflineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    App.get().getJobManager().addJob(new SetAvailabilityStatusJob(false, isChecked ? BookingModel.AVAILABILITY_STATUS.ONLINE.ordinal() : BookingModel.AVAILABILITY_STATUS.OFFLINE.ordinal()));
                }
            }
        });

        mainScreenSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentBookingCountJob(); // fetch current booking count
                getTotalEarningJob(); // fetch total earning
                onItemsLoadComplete(); // gone refresh ring
            }
        });
    }

    /**
     * mainScreenSwipeToRefresh : set to false refreshing content
     */
    private void onItemsLoadComplete() {
        mainScreenSwipeToRefresh.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CURRENT_RESERVATION) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentBookingCountJob(); // fetch latest data of current booking count
                getTotalEarningJob(); // get total earning
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.UpdateReservationScreenEventStatus eventStatus) {
        log.verbose("UpdateReservationScreenEventStatus ");
        eventStatus.removeStickySelf();
        int status = eventStatus.getStatus();
        boolean isStatusMatched = status == BookingModel.BOOKING_STATUS.COMPLETED.ordinal();
        if (isStatusMatched) {
            getCurrentBookingCountJob(); // fetch latest data of current booking count
            getTotalEarningJob(); // get total earning
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.GetCurrentBookingCount getCurrentBookingCount) {
        log.verbose("getCurrentBookingCount isSuccess " + getCurrentBookingCount.isSuccess);
        getCurrentBookingCount.removeStickySelf();
        String bookingCountStr = getResources().getString(R.string.current_booking_count);
        if (getCurrentBookingCount.isSuccess) {
            int count = getCurrentBookingCount.bookingCount;
            String strBookingCount = String.format(bookingCountStr, count);
            currentBookingCount.setText(strBookingCount);
        } else {
            String strBookingCount = String.format(bookingCountStr, 0);
            currentBookingCount.setText(strBookingCount);
            if (getCurrentBookingCount.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) || getCurrentBookingCount.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase())) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(MainActivity.this);
            } else {
                showLongSnackBar(getCurrentBookingCount.errorMessage);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.GetTotalEarningEventStatus getTotalEarningEventStatus) {
        log.verbose("GetTotalEarningEventStatus isSuccess " + getTotalEarningEventStatus.isSuccess);
        getTotalEarningEventStatus.removeStickySelf();
        totalEarningAmount.setText(String.format(Locale.getDefault(), getString(R.string.txt_rate), getTotalEarningEventStatus.totalEarning));
        if (!getTotalEarningEventStatus.isSuccess) {

            if (getTotalEarningEventStatus.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) || getTotalEarningEventStatus.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase())) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(MainActivity.this);
            } else {

                showLongSnackBar(getTotalEarningEventStatus.errorMessage);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.SetAvailableStatusEventStatus setAvailableStatusEventStatus) {
        log.verbose("setAvailableStatusEventStatus isSuccess " + setAvailableStatusEventStatus.isSuccess);
        setAvailableStatusEventStatus.removeStickySelf();
        if (setAvailableStatusEventStatus.isSuccess) {
            if (!setAvailableStatusEventStatus.isSilentLogin) {
                showLongSnackBar(getResources().getString(R.string.updated_successfully_msg));
            }
            onlineOfflineSwitch.setChecked(setAvailableStatusEventStatus.isAvailable != 0);
            switchButtonMsg.setText(setAvailableStatusEventStatus.isAvailable != 0 ? getResources().getString(R.string.model_online_msg) : getResources().getString(R.string.model_offline_msg));
        } else {
            onlineOfflineSwitch.setChecked(false);
            if (!setAvailableStatusEventStatus.isSilentLogin) {
                if (setAvailableStatusEventStatus.errorCode == ErrorView.ERROR_CODE_IN_EVENT) {
                    DialogHelper.showOkDialog(this, setAvailableStatusEventStatus.errorMessage, null);
                } else {
                    showLongSnackBar(setAvailableStatusEventStatus.errorMessage);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.LogoutEventStatus logoutEventStatus) {
        log.verbose("logoutEventStatus " + logoutEventStatus.isSuccess);
        logoutEventStatus.removeStickySelf();
        if (logoutEventStatus.isSuccess) {
            showLongSnackBar(getResources().getString(R.string.logout_successfully_msg));
        } else {
            if (logoutEventStatus.errorMessage.equalsIgnoreCase(ErrorView.CUSTOM_ERROR_MESSAGE_ALREADY_LOGOUT)) {
                imgUserProfileBtn.setVisibility(View.GONE);
            }
            showLongSnackBar(logoutEventStatus.errorMessage);
        }
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.gps_disable))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.turn_on),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                dialog.cancel();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * askToAllowGPSOn : method to show gps enable dialog when gps is not enabled at device side
     */
    public void settingRequestForGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.e("here", "values " + String.valueOf(location.getLatitude()));

            SharedPreferences.Editor editor = App.get().getPreferenceEditor();
            editor.putString(SharePreferenceKeyConstants.CURRENT_LAT, String.valueOf(location.getLatitude()));
            editor.putString(SharePreferenceKeyConstants.CURRENT_LONG, String.valueOf(location.getLongitude()));
            editor.commit();
            editor.apply();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void sendRegistrationToken(String fcm_token) {
        App.get().getJobManager().addJob(new SendTokenJob(fcm_token));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showBookedAlertDialog(intent.getIntExtra("bookingId", 0), intent.getStringExtra("message"));
        }
    };

    @Override
    public void onLocationChanged(Location location) {

    }


    public void initLocationTracking() {
        String appToken = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int userId = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        if (!TextUtils.isEmpty(appToken) && userId != 0) {
            if (!isMyServiceRunning(LocationTrackingService.class)) {
                log.debug("startLocationTracking");
                startService(new Intent(this, LocationTrackingService.class));
            }
        }
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
}
