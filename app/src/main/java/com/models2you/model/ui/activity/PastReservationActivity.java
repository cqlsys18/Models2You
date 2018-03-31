package com.models2you.model.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.event.Events;
import com.models2you.model.job.GetReservationJob;
import com.models2you.model.model.BookingModel;
import com.models2you.model.ui.adapter.ReservationAdapter;
import com.models2you.model.ui.base.BaseAppCompatActivity;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amitsingh on 4/24/2017.
 * Past Reservation Screen to show past bookings list
 */

public class PastReservationActivity extends BaseAppCompatActivity {
    private static final LogFactory.Log log = LogFactory.getLog(PastReservationActivity.class);
    private static final int REQUEST_CODE_RESERVATION_DETAIL = 121;

    @BindView(R.id.currentResRecyclerView) RecyclerView pastResRecyclerView;
    @BindView(R.id.imgHome) ImageView imgHome;
    @BindView(R.id.currentReservationSwipeRefreshLayout) SwipeRefreshLayout pastReservationSwipeRefreshLayout;
    @BindView(R.id.previousBookingTitleText) TextView pastBookingTitleText;

    @Override
    protected boolean needScreenFinishedWithResultOk() { return true; }

    @Override
    public void onOptionsMenuPastReservationClicked(View view) {
        if(frameLayout.getParent()!=null) {
            ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_current);
        ButterKnife.bind(this);
        initViews();
        getReservationJobData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!Utils.isNetworkAvailable(PastReservationActivity.this)){
            showLongSnackBar(getResources().getString(R.string.error_check_internet));
        }
    }

    private void initViews() {
        imgUserProfileBtn.setVisibility(View.VISIBLE);
        imgHome.setImageResource(R.drawable.back_button);
        pastBookingTitleText.setText(getString(R.string.past_booking_title_text));

        pastReservationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getReservationJobData();
            }
        });
    }

    void onItemsLoadComplete() {
        pastReservationSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        PastReservationActivity.this.finish();
        overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
    }
    /**
     * getReservationJobData : method to get Job response from Reservation Job
     */
    private void getReservationJobData() {
        showProgressDialog(); // show smiley loading view when previous reservation job called
        List<Integer> bookingType = new ArrayList<>();
        bookingType.add(BookingModel.BOOKING_STATUS.ACCEPTED.ordinal());
        App.get().getJobManager().addJob(new GetReservationJob(BookingModel.BOOKING_CATEGORY.PREVIOUS, bookingType));
        onItemsLoadComplete();
    }

    /**
     * showProgressDialog : method to show Loading View when
     * previous reservation job called
     */
    private void showProgressDialog() {
        progressDialog = Utils.showProgressBar(this , getString(R.string.please_wait));
        progressDialog.setCancelable(true);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESERVATION_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                getReservationJobData();
            }
        }
    }

    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Events.GetPreviousReservationModelListEventSticky event) {
        log.verbose("GetPreviousReservationModelListEventSticky isSuccess " + event.isSuccess);
        event.removeStickySelf();
        hideProgressDialog(); // hide smiley loading view when response come
        if (event.isSuccess) {
            final List<BookingModel> reservationList = event.reservationModelList;
            if (reservationList != null && !reservationList.isEmpty()) {
                new SyncReservationModelAsyncTask(reservationList).execute();
            }
        } else {
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(PastReservationActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    public void onEventMainThread(Events.UpdateReservationScreenEventStatus eventStatus){
        log.verbose("UpdateReservationScreenEventStatus ");
        eventStatus.removeStickySelf();
        int status = eventStatus.getStatus();
        boolean isStatusMatched = status == BookingModel.BOOKING_STATUS.COMPLETED.ordinal() ||
                status == BookingModel.BOOKING_STATUS.CANCELED.ordinal() ||
                status == BookingModel.BOOKING_STATUS.DENIED.ordinal();
        if (isStatusMatched) {
            getReservationJobData();
        }
    }

    private class SyncReservationModelAsyncTask extends AsyncTask<Void, Void, List<BookingModel>> {
        private final List<BookingModel> reservationList;

        SyncReservationModelAsyncTask(List<BookingModel> reservationList) {
            this.reservationList = reservationList;
        }

        @Override
        protected List<BookingModel> doInBackground(Void... params) {
            List<BookingModel> filteredReservationList = new ArrayList<>();
            long currentTimeMillis = Utils.getCurrentTimeInMillis();
            String currentDate = Utils.convertTimeMillisToDateFormat(currentTimeMillis, Constants.simpleDateFormat);
            Date currentDateObj =  Utils.convertStringToDate(currentDate , Constants.simpleDateFormat);
            if (currentDateObj != null) {
                for (BookingModel bookingModel : reservationList) {
                    String arrivedTime = bookingModel.arrivedTime;
                    Date arriveTimeInDateFormat = null;
                    if (!TextUtils.isEmpty(arrivedTime) && !arrivedTime.equals(Constants.notAllowedDateFormat)) {
                        //arrivedTime = Utils.convertSimpleDateFormat(arrivedTime , Constants.desiredFormattedDateToSend , Constants.simpleDateFormat);
                        arriveTimeInDateFormat = Utils.convertStringToDate(arrivedTime, Constants.desiredFormattedDateToSend);
                    }
                    if (arriveTimeInDateFormat == null) {
                        String appointmentTime = bookingModel.appointmentTime;
                        //appointmentTime = Utils.convertSimpleDateFormat(appointmentTime , Constants.desiredFormattedDateToSend , Constants.simpleDateFormat);
                        arriveTimeInDateFormat =  Utils.convertStringToDate(appointmentTime, Constants.desiredFormattedDateToSend);
                    }

                    if (arriveTimeInDateFormat != null && arriveTimeInDateFormat.before(currentDateObj)) {
                        filteredReservationList.add(bookingModel);
                    }
                }
                return filteredReservationList;
            }
            return filteredReservationList;
        }

        @Override
        protected void onPostExecute(List<BookingModel> bookingModelList) {
            showPastReservationData(bookingModelList);
        }
    }

    private void showPastReservationData(List<BookingModel> reservationList) {
        pastResRecyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(PastReservationActivity.this);
        pastResRecyclerView.setLayoutManager(manager);
        ReservationAdapter reservationAdapter = new ReservationAdapter(PastReservationActivity.this);
        reservationAdapter.setData(reservationList);
        pastResRecyclerView.setAdapter(reservationAdapter);
        reservationAdapter.notifyDataSetChanged();
        reservationAdapter.setReservationItemClickListener(new ReservationAdapter.ReservationItemClickListener() {
            @Override
            public void onReservationItemClicked(int position) {
                log.verbose("ReservationAdapter onReservationItemClicked position " + position);
                progressDialog = Utils.showProgressBar(PastReservationActivity.this , getResources().getString(R.string.please_wait));
                progressDialog.dismiss();
                Intent toReservationDetailIntent = new Intent(PastReservationActivity.this, ReservationDetailActivity.class);
                toReservationDetailIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, position);
                startActivityForResult(toReservationDetailIntent, REQUEST_CODE_RESERVATION_DETAIL);
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            }
        });
    }
}
