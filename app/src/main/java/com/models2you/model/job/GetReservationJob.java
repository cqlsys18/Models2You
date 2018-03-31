package com.models2you.model.job;

import android.text.TextUtils;


import com.models2you.model.app.App;
import com.models2you.model.event.Events;
import com.models2you.model.job.base.Priority;
import com.models2you.model.model.BookingModel;
import com.models2you.model.model.ReservationModel;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by chandrakant on 8/31/2016.
 * GetReservationJob to get Previous Reservation List
 */
public class GetReservationJob extends Job {
    private static final LogFactory.Log log = LogFactory.getLog(GetReservationJob.class);

    private List<Integer> bookingType;
    private BookingModel.BOOKING_CATEGORY bookingCategory;

    public GetReservationJob(BookingModel.BOOKING_CATEGORY bookingCategory, List<Integer> bookingType) {
        super(new Params(Priority.HIGH).groupBy("current_previous_reservation_job"));
        this.bookingType = bookingType;
        this.bookingCategory = bookingCategory;
    }

    @Override
    public void onAdded() {
        log.verbose("onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        log.verbose("onJobRun booking type " + bookingType , "bookingCategory " + bookingCategory);
        String token = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.APP_TOKEN, "");
        int id = App.get().getDefaultAppSharedPreferences().getInt(SharePreferenceKeyConstants.USER_ID, 0);
        if (!TextUtils.isEmpty(token)) {
            Call<ResponseViews.CurrentAndPreviousReservationResponse> currentAndPreviousReservationResponseCall = App.get().getApiFactory().getCommonApi().getCurrentAndPreviousReservation(token, id, bookingType);
            log.verbose("currentAndPreviousReservationResponseCall " + currentAndPreviousReservationResponseCall);
            Response<ResponseViews.CurrentAndPreviousReservationResponse> response = currentAndPreviousReservationResponseCall.execute();
            log.verbose("currentAndPreviousReservationResponseCall response " + response);
            ResponseViews.CurrentAndPreviousReservationResponse currentAndPreviousReservationResponse = response.body();
            boolean isSuccess = currentAndPreviousReservationResponse.isSuccess;
            log.verbose("currentAndPreviousReservationResponse isSuccess" + isSuccess);
            if (isSuccess) {
                List<ResponseViews.Booking> bookings = currentAndPreviousReservationResponse.bookings;
                log.verbose("bookingsList " + bookings);
                ReservationModel reservationModel = new ReservationModel(isSuccess , bookings);
                List<BookingModel> bookingModelList = reservationModel.getReservationModelList();
                if (bookingCategory == BookingModel.BOOKING_CATEGORY.CURRENT) {
                    App.get().getEventBus().postSticky(new Events.GetCurrentReservationModelListEventSticky(isSuccess, bookingModelList));
                } else if (bookingCategory == BookingModel.BOOKING_CATEGORY.PREVIOUS) {
                    App.get().getEventBus().postSticky(new Events.GetPreviousReservationModelListEventSticky(isSuccess, bookingModelList));
                } else {
                    App.get().getEventBus().postSticky(new Events.getPendingReservationModelListEventSticky(isSuccess, bookingModelList));
                }
            } else {
                if (bookingCategory == BookingModel.BOOKING_CATEGORY.CURRENT) {
                    App.get().getEventBus().postSticky(new Events.GetCurrentReservationModelListEventSticky(isSuccess, currentAndPreviousReservationResponse.errorCode, currentAndPreviousReservationResponse.errorMessage));
                } else if (bookingCategory == BookingModel.BOOKING_CATEGORY.PREVIOUS) {
                    App.get().getEventBus().postSticky(new Events.GetPreviousReservationModelListEventSticky(isSuccess, currentAndPreviousReservationResponse.errorCode, currentAndPreviousReservationResponse.errorMessage));
                } else {
                    App.get().getEventBus().postSticky(new Events.getPendingReservationModelListEventSticky(isSuccess, currentAndPreviousReservationResponse.errorCode, currentAndPreviousReservationResponse.errorMessage));
                }
            }
        } else {
            if (bookingCategory == BookingModel.BOOKING_CATEGORY.CURRENT) {
                App.get().getEventBus().postSticky(new Events.GetCurrentReservationModelListEventSticky(false, ErrorView.ERROR_CODE_NOT_VALID_TOKEN, ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN));
            } else if (bookingCategory == BookingModel.BOOKING_CATEGORY.PREVIOUS) {
                App.get().getEventBus().postSticky(new Events.GetPreviousReservationModelListEventSticky(false, ErrorView.ERROR_CODE_NOT_VALID_TOKEN, ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN));
            } else {
                App.get().getEventBus().postSticky(new Events.getPendingReservationModelListEventSticky(false, ErrorView.ERROR_CODE_NOT_VALID_TOKEN, ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN));
            }
        }
    }

    @Override
    protected void onCancel() {
        log.verbose("onCancel");
        if (bookingCategory == BookingModel.BOOKING_CATEGORY.CURRENT) {
            App.get().getEventBus().postSticky(new Events.GetCurrentReservationModelListEventSticky(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
        } else  if (bookingCategory == BookingModel.BOOKING_CATEGORY.PREVIOUS) {
            App.get().getEventBus().postSticky(new Events.GetPreviousReservationModelListEventSticky(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
        } else {
            App.get().getEventBus().postSticky(new Events.getPendingReservationModelListEventSticky(false, ErrorView.CUSTOM_ERROR_CODE, ErrorView.CUSTOM_ERROR_MESSAGE));
        }

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        log.verbose("shouldReRunOnThrowable");
        return false;
    }
}
