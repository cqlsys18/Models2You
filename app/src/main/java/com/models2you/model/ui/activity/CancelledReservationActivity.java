package com.models2you.model.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import java.util.List;

import butterknife.BindView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amitsingh on 4/26/2017.
 * Cancelled reservation screen
 */

public class CancelledReservationActivity extends BaseAppCompatActivity {
    private static final LogFactory.Log log = LogFactory.getLog(CancelledReservationActivity.class);

    @BindView(R.id.previousResRecyclerView) RecyclerView cancelledBookingRecyclerView;
    @BindView(R.id.imgHome) ImageView imgHome;
    @BindView(R.id.previousReservationSwipeRefreshLayout) SwipeRefreshLayout cancelledReservationSwipeRefreshLayout;
    @BindView(R.id.previousBookingTitleText) TextView cancelledBookingTitleText;

    @Override
    protected boolean needToFinishCurrentActivity() { return true; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_previous);
        ButterKnife.bind(this);
        initViews();
        getReservationJobData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!Utils.isNetworkAvailable(this)){
            showLongSnackBar(getResources().getString(R.string.error_check_internet));
        }
    }

    private void initViews() {
        imgUserProfileBtn.setVisibility(View.VISIBLE);
        imgHome.setImageResource(R.drawable.back_button);
        cancelledBookingTitleText.setText(getString(R.string.cancel_booking_title_text));
        cancelledReservationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getReservationJobData();
            }
        });
    }

    void onItemsLoadComplete() {
        cancelledReservationSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * getReservationJobData : method to get Job response from Reservation Job
     */
    private void getReservationJobData() {
        List<Integer> bookingType = new ArrayList<>();
        bookingType.add(BookingModel.BOOKING_STATUS.CANCELED.ordinal());
        bookingType.add(BookingModel.BOOKING_STATUS.DENIED.ordinal());
        showDialog(); // show smiley loading view when previous reservation job called
        App.get().getJobManager().addJob(new GetReservationJob(BookingModel.BOOKING_CATEGORY.PREVIOUS, bookingType));
        onItemsLoadComplete();
    }

    /**
     * showDialog : method to show Loading View when
     * previous reservation job called
     */
    private void showDialog() {
        progressDialog = Utils.showProgressBar(this, getString(R.string.please_wait));
    }

    private void hideLoadingView() {
        progressDialog.dismiss();
    }

    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Events.GetPreviousReservationModelListEventSticky event) {
        log.verbose("GetPreviousReservationModelListEventSticky isSuccess " + event.isSuccess);
        event.removeStickySelf();
        hideLoadingView(); // hide smiley loading view when response come
        if (event.isSuccess) {
            final List<BookingModel> reservationList = event.reservationModelList;
            if (reservationList != null && !reservationList.isEmpty()) {
                cancelledBookingRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager manager = new LinearLayoutManager(this);
                cancelledBookingRecyclerView.setLayoutManager(manager);
                ReservationAdapter reservationAdapter = new ReservationAdapter(this);
                reservationAdapter.setData(reservationList);
                cancelledBookingRecyclerView.setAdapter(reservationAdapter);
                reservationAdapter.notifyDataSetChanged();

                reservationAdapter.setReservationItemClickListener(new ReservationAdapter.ReservationItemClickListener() {
                    @Override
                    public void onReservationItemClicked(int position) {
                        log.verbose("ReservationAdapter onReservationItemClicked position " + position);
                        Intent toReservationDetailIntent = new Intent(CancelledReservationActivity.this, ReservationDetailActivity.class);
                        toReservationDetailIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, position);
                        startActivity(toReservationDetailIntent);
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    }
                });
            } else {
                cancelledBookingRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(CancelledReservationActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }
}
