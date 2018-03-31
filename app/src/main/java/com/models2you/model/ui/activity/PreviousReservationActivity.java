package com.models2you.model.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import butterknife.ButterKnife;

/**
 * Created by amitsingh on 9/27/2016.
 * Previous Reservation Activity to show Previous Reservation details
 */
public class PreviousReservationActivity extends BaseAppCompatActivity {

    private static final LogFactory.Log log = LogFactory.getLog(PreviousReservationActivity.class);

    @BindView(R.id.previousResRecyclerView)
    RecyclerView previousBookingRecyclerView;
    @BindView(R.id.imgHome)
    ImageView imgHome;
    @BindView(R.id.previousReservationSwipeRefreshLayout)
    SwipeRefreshLayout previousReservationSwipeRefreshLayout;

    @Override
    protected boolean needToFinishCurrentActivity() { return true; }

    @Override
    public void onOptionsMenuCompletedReservationClicked(View view) {
        if(frameLayout.getParent()!=null) {
            ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
        }
    }

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
        if(!Utils.isNetworkAvailable(PreviousReservationActivity.this)){
            showLongSnackBar(getResources().getString(R.string.error_check_internet));
        }
    }

    private void initViews() {
        imgUserProfileBtn.setVisibility(View.VISIBLE);
        imgHome.setImageResource(R.drawable.back_button);

        previousReservationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getReservationJobData();
            }
        });
    }

    void onItemsLoadComplete() {
        previousReservationSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * getReservationJobData : method to get Job response from Reservation Job
     */
    private void getReservationJobData() {
        List<Integer> bookingType = new ArrayList<>();
        bookingType.add(BookingModel.BOOKING_STATUS.COMPLETED.ordinal());
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
                previousBookingRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager manager = new LinearLayoutManager(PreviousReservationActivity.this);
                previousBookingRecyclerView.setLayoutManager(manager);
                ReservationAdapter reservationAdapter = new ReservationAdapter(PreviousReservationActivity.this);
                reservationAdapter.setData(reservationList);
                previousBookingRecyclerView.setAdapter(reservationAdapter);
                reservationAdapter.notifyDataSetChanged();

                reservationAdapter.setReservationItemClickListener(new ReservationAdapter.ReservationItemClickListener() {
                    @Override
                    public void onReservationItemClicked(int position) {
                        log.verbose("ReservationAdapter onReservationItemClicked position " + position);
                        Intent toReservationDetailIntent = new Intent(PreviousReservationActivity.this, ReservationDetailActivity.class);
                        toReservationDetailIntent.putExtra(Constants.INTENT_EXTRA_BOOKING_MODEL_POSITION, position);
                        startActivity(toReservationDetailIntent);
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    }
                });
            } else {
                previousBookingRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(PreviousReservationActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }

}
