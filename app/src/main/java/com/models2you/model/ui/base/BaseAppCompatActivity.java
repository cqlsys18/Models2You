package com.models2you.model.ui.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.event.Events;
import com.models2you.model.job.LogoutJob;
import com.models2you.model.job.UpdateReservationStatusBookingJob;
import com.models2you.model.model.BookingModel;
import com.models2you.model.ui.activity.CancelledReservationActivity;
import com.models2you.model.ui.activity.CurrentReservationActivity;
import com.models2you.model.ui.activity.ManagePhotoActivity;
import com.models2you.model.ui.activity.PastReservationActivity;
import com.models2you.model.ui.activity.PendingReservationActivity;
import com.models2you.model.ui.activity.PreviousReservationActivity;
import com.models2you.model.ui.activity.ReservationDetailActivity;
import com.models2you.model.ui.activity.UpdateProfileActivity;
import com.models2you.model.util.DialogHelper;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseAppCompatActivity extends AppCompatActivity {
    private static final LogFactory.Log log = LogFactory.getLog(BaseAppCompatActivity.class);
    protected static final int REQUEST_CODE_CURRENT_RESERVATION = 452;
    private View toolBarCustomElevation;
    private final SparseArray<Long> viewClickTimeStampSparseArray = new SparseArray<>();
    private ViewGroup snackBarAnchorView;
    private View mTouchOutsideView; // view object for outside touch
    private OnTouchOutsideViewListener mOnTouchOutsideViewListener; // listener

    //
    protected FrameLayout frameLayout;
    protected ImageView imgUserProfileBtn;
    protected ImageView btnHome, imgLogo;
    protected RelativeLayout main_layout;
    protected ProgressDialog progressDialog;

    public enum SELECTED_TAB_ITEMS {
        ONLINE_MODELS,
        ALL_MODELS,
        VIEW_CART,
        BOOKINGS,
        PROFILE
    }

    public static final String INTENT_EXTRA_RESERVATION_BOOKING_TYPE = "bookingType";

    protected boolean needToFinishCurrentActivity() {
        return false;
    }

    protected boolean needScreenFinishedWithResultOk() {
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public final void setContentView(@LayoutRes int layoutResID) {
        //Override default method and add child layout in content container layout.
        ViewGroup container = (ViewGroup) findViewById(R.id.base_activity_content_layout);
        getLayoutInflater().inflate(layoutResID, container, true);
        snackBarAnchorView = container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_layout);
        btnHome = (ImageView) findViewById(R.id.imgHome);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        main_layout = (RelativeLayout) findViewById(R.id.baseHeaderLayout);
        imgUserProfileBtn = (ImageView) findViewById(R.id.imgUserProfileBtn);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.option_menu_list, (ViewGroup) getWindow().getDecorView().getRootView(), false);
        frameLayout = (FrameLayout) view.findViewById(R.id.headerMenuLayout);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeButtonClicked(v);
            }
        });
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeButtonClicked(v);
            }
        });
        imgUserProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frameLayout.getParent() != null) {
                    ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
                } else {
                    int height = v.getHeight();
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.RIGHT;
                    params.setMargins(5, height + 55, 10, 5);
                    addContentView(frameLayout, params);
                    frameLayout.setVisibility(View.VISIBLE);
                    setOnTouchOutsideViewListener(frameLayout, onOutsideTouchListener); // view , listener to listen when outside view touched
                }
            }
        });
        //Setting theme in recent tasks
        ActivityManager.TaskDescription taskDesc;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), null, getResources().getColor(R.color.appcolorActionBarBackground));
            setTaskDescription(taskDesc);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        App.get().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("stop", "called");
        App.get().getEventBus().unregister(this);
    }

    public void showBookedAlertDialog(final int bookingId, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.alert_dialog_view), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(BaseAppCompatActivity.this, ReservationDetailActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, bookingId);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            }
        }).setNegativeButton(getResources().getString(R.string.alert_dialog_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                showDenyAlertDialog(bookingId); // show deny alert dialog
            }
        });
        builder.create();
        if (!isFinishing()) {
            builder.show();
        }
    }

    /**
     * showAlertMessageWithOk : method to show Alert for Failed
     * to show location tracking automatically
     *
     * @param message : message
     */
    protected void showAlertMessageWithOk(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });
        if (!isFinishing()) {
            builder.show();
        }
    }

    private void showDenyAlertDialog(final int bookingId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.res_details_deny);
        builder.setMessage(getResources().getString(R.string.booking_detail_alert_dialog_deny_msg));
        builder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                // deny job called.
                App.get().getJobManager().addJob(new UpdateReservationStatusBookingJob(bookingId, BookingModel.BOOKING_STATUS.DENIED.ordinal(), false, ""));
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        if (!isFinishing()) {
            builder.show();
        }
    }

    /**
     * onHomeButtonClicked : method to get event when
     * Home button_selector_blue clicked
     *
     * @param v : view
     */
    protected void onHomeButtonClicked(View v) {
        if (needScreenFinishedWithResultOk()) {
            setResult(RESULT_OK);
            finish();
        } else if (needToFinishCurrentActivity()) {
            finish();
        }
        overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
    }

    /**
     * onOptionsMenuUpdateProfileClicked : method to open UpdateUserProfileActivity when option menu update profile clicked
     *
     * @param view : view
     */
    public void onOptionsMenuUpdateProfileClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent updateProfileLaunchIntent = new Intent(this, UpdateProfileActivity.class);
            startActivity(updateProfileLaunchIntent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * onOptionsMenuManagePhotoClicked : method to open Manage Photo Screen
     * when option menu Manage Photo clicked
     *
     * @param view : view
     */
    public void onOptionsMenuManagePhotoClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent managePhotoLaunchIntent = new Intent(this, ManagePhotoActivity.class);
            startActivity(managePhotoLaunchIntent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * onOptionsMenuPendingReservationClicked : method to open Pending Reservation Screen
     * when option menu Pending Reservation clicked
     *
     * @param view : view
     */
    public void onOptionsMenuPendingReservationClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent pendingReservationLaunchIntent = new Intent(this, PendingReservationActivity.class);
            startActivity(pendingReservationLaunchIntent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * onOptionsMenuCurrentReservationClicked : method to open Current Reservation
     * when option menu current reservation clicked
     *
     * @param view : view
     */
    public void onOptionsMenuCurrentReservationClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent currentReservationLaunchIntent = new Intent(this, CurrentReservationActivity.class);
            startActivityForResult(currentReservationLaunchIntent, REQUEST_CODE_CURRENT_RESERVATION);
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }


    /**
     * onOptionsMenuCompletedReservationClicked : method to open Previous Reservation Screen
     * when option menu Previous Reservation clicked
     *
     * @param view : view
     */
    public void onOptionsMenuCompletedReservationClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent previousReservationLaunchIntent = new Intent(this, PreviousReservationActivity.class);
            startActivity(previousReservationLaunchIntent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * onOptionsMenuPastReservationClicked : method to open Past Reservation Screen
     * when option menu Past Reservation clicked
     *
     * @param view : view
     */
    public void onOptionsMenuPastReservationClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent pastReservationLaunchIntent = new Intent(this, PastReservationActivity.class);
            startActivity(pastReservationLaunchIntent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * method to open cancelled reservation screen
     *
     * @param view : view
     */
    public void onOptionsMenuCancelledReservationClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            Intent intent = new Intent(BaseAppCompatActivity.this, CancelledReservationActivity.class);
            startActivity(intent);
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * onOptionsMenuLogoutClicked : method to logout job call when option menu logout clicked
     *
     * @param view : view
     */
    public void onOptionsMenuLogoutClicked(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            if (frameLayout.getParent() != null) {
                ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
            }
            // logout job called // Not checking for internet anymore.
            /*if (!Utils.isNetworkAvailable(this)) {
                showLongSnackBar(getResources().getString(R.string.error_check_internet));
            }else {*/
            String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
            if (!TextUtils.isEmpty(token)) {
                App.get().getJobManager().addJob(new LogoutJob());
            } else {
                showLongSnackBar(getResources().getString(R.string.error_user_already_logout));
            }
            //}
            if (needScreenFinishedWithResultOk()) {
                setResult(RESULT_OK);
                finish();
            } else if (needToFinishCurrentActivity()) {
                finish();
            }
        }
    }

    /**
     * onOutsideTouchListener : listen touch outside of view
     */
    protected OnTouchOutsideViewListener onOutsideTouchListener = new OnTouchOutsideViewListener() {
        @Override
        public void onTouchOutside(View view, MotionEvent event) {
            log.verbose("view " + view);
            view.setVisibility(View.GONE);
        }
    };


    /**
     * Sets a listener that is being notified when the user has tapped outside a given view. To remove the listener,
     * <p/>
     * This is useful in scenarios where a view is in edit mode and when the user taps outside the edit mode shall be
     * stopped.
     *
     * @param view                       : view
     * @param onTouchOutsideViewListener : listener
     */
    protected void setOnTouchOutsideViewListener(View view, OnTouchOutsideViewListener onTouchOutsideViewListener) {
        mTouchOutsideView = view;
        mOnTouchOutsideViewListener = onTouchOutsideViewListener;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Notify touch outside listener if user tapped outside a given view
            if (mOnTouchOutsideViewListener != null && mTouchOutsideView != null
                    && mTouchOutsideView.getVisibility() == View.VISIBLE) {
                Rect viewRect = new Rect();
                mTouchOutsideView.getGlobalVisibleRect(viewRect);
                if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    mOnTouchOutsideViewListener.onTouchOutside(mTouchOutsideView, ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Interface definition for a callback to be invoked when a touch event has occurred outside a formerly specified
     * view. See {@link #setOnTouchOutsideViewListener(View, OnTouchOutsideViewListener).}
     */
    public interface OnTouchOutsideViewListener {
        /**
         * Called when a touch event has occurred outside a given view.
         *
         * @param view  The view that has not been touched.
         * @param event The MotionEvent object containing full information about the event.
         */
        void onTouchOutside(View view, MotionEvent event);
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        if (shouldOverrideActivityTransition()) {
            super.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public boolean shouldOverrideActivityTransition() {
        return true;
    }

    /**
     * Show long time SnackBar
     *
     * @param text text to show
     * @return shown SnackBar instance
     */
    public final Snackbar showLongSnackBar(String text) {
        Snackbar snackbar = makeSnackBar(text, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    /**
     * Show short time SnackBar
     *
     * @param text text to show
     * @return shown SnackBar instance
     */
    public final Snackbar showShortSnackBar(String text) {
        Snackbar snackbar = makeSnackBar(text, Snackbar.LENGTH_SHORT);
        snackbar.show();
        return snackbar;
    }

    public final void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Return theme based SnackBar
     *
     * @param text   text to show
     * @param length SnackBar.LENGTH_LONG type param
     * @return SnackBar object to show
     */
    public final Snackbar makeSnackBar(String text, int length) {
        Snackbar snackbar = Snackbar.make(snackBarAnchorView, text, length);
        TextView textView = (TextView) (snackbar.getView()).findViewById(R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.textWhite));
        return snackbar;
    }

    /**
     * Preventing multiple clicks. (validate within 1000 millis)
     *
     * @param view need to prevent multiple clicks on.
     */
    protected boolean isTooEarlyMultipleClicks(@NonNull View view) {
        return isTooEarlyMultipleClicks(view, 1000); //default 1 sec
    }

    // Enable back button_selector_blue in Action bar
    protected void setActionBarBackEnable() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } else {
            log.debug("setActionBarBackEnable, can not find supportActionBar");
        }
    }

    /**
     * Preventing multiple clicks.
     *
     * @param view        need to prevent multiple clicks on.
     * @param delayMillis millis delta to validate multiple click events
     */
    protected boolean isTooEarlyMultipleClicks(@NonNull View view, int delayMillis) {
        long lastClickTime = viewClickTimeStampSparseArray.get(view.getId(), 0L);
        long timeStamp = System.currentTimeMillis();
        if (lastClickTime + delayMillis > timeStamp) {
            log.debug("View clicked too early" + view);
            return true;
        }
        viewClickTimeStampSparseArray.put(view.getId(), timeStamp);
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.GCMEventStickyResult gcmEventStickyResult) {
        gcmEventStickyResult.removeStickySelf();
        showBookedAlertDialog(gcmEventStickyResult.bookingId, gcmEventStickyResult.message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(Events.ShowAlertMessageOnNotificationEventStatus eventStatus) {
        log.verbose("ShowAlertMessageOnNotificationEventStatus msg " + eventStatus.getMessage());
        eventStatus.removeStickySelf();
        if (!isFinishing()) {
            DialogHelper.showOkDialog(this, eventStatus.getMessage(), null);
        }
    }
}