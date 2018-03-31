package com.models2you.model.event;

import com.models2you.model.app.App;
import com.models2you.model.model.BookingModel;
import com.models2you.model.rest.models.ResponseViews;

import java.util.List;

/**
 * Created by Amit on 9/28/2016.
 * Event class to send events from Job
 */
public class Events {

    private Events() {
    }

    /**
     * Base class for StickyEvents
     */
    public static class BaseStickyEvent {

        public void removeStickySelf() {
            App.get().getEventBus().removeStickyEvent(this);
        }
    }


    public static class UpdateStatusOfCurrentBookingEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public int status;
        public String errorMessage;
        public int errorCode;

        public UpdateStatusOfCurrentBookingEventStatus(boolean isSuccess, int status) {
            this.isSuccess = isSuccess;
            this.status = status;
        }

        public UpdateStatusOfCurrentBookingEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }


    /* forget password Sticky event */
    public static class ForgetPasswordEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public ForgetPasswordEventStatus(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public ForgetPasswordEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

     /* forget password Sticky event */
    public static class RenewTokenExpiryEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public RenewTokenExpiryEventStatus(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public RenewTokenExpiryEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    /* location update Sticky event */
    public static class LocationUpdateEvent extends BaseStickyEvent{
        private boolean isSuccess;
        private String errorMessage;
        private double lat;
        private double lon;
        private int errorCode;

        public LocationUpdateEvent(boolean isSuccess, int errorCode, String errorMessage, double lat, double lon) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.lat = lat;
            this.lon = lon;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }

    /**
     * LogoutEventStatus : logout event status from logout job
     */
    public static class LogoutEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public LogoutEventStatus(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public LogoutEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
    /* show auto cancel alerts on notification comes */
    public static class ShowAlertMessageOnNotificationEventStatus extends BaseStickyEvent {
        private String message;

        public ShowAlertMessageOnNotificationEventStatus(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    public static class UpdateUserProfileEventSticky extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public UpdateUserProfileEventSticky(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public UpdateUserProfileEventSticky(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public static class SignUpEventStickyResult extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public SignUpEventStickyResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public SignUpEventStickyResult(boolean isSuccess , String error , int errorCode) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    public static class LoginEventStickyResult extends BaseStickyEvent{
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public LoginEventStickyResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public LoginEventStickyResult(boolean isSuccess , String error , int errorCode) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    /**
     * GetCurrentReservationModelListEventSticky : get current Event Sticky Result
     */
    public static class GetCurrentReservationModelListEventSticky extends BaseStickyEvent {

        public List<BookingModel> reservationModelList;
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public GetCurrentReservationModelListEventSticky(boolean isSuccess, List<BookingModel> reservationModelList) {
            this.isSuccess = isSuccess;
            this.reservationModelList = reservationModelList;
        }

        public GetCurrentReservationModelListEventSticky(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public static class ChangePasswordEventStickyResult extends BaseStickyEvent {
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public ChangePasswordEventStickyResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public ChangePasswordEventStickyResult(boolean isSuccess, String error, int errorCode) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    /**
     * GetPreviousReservationModelListEventSticky : get previous reservation sticky result
     */
    public static class GetPreviousReservationModelListEventSticky extends BaseStickyEvent {

        public List<BookingModel> reservationModelList;
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public GetPreviousReservationModelListEventSticky(boolean isSuccess, List<BookingModel> reservationModelList) {
            this.isSuccess = isSuccess;
            this.reservationModelList = reservationModelList;
        }

        public GetPreviousReservationModelListEventSticky(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * getPendingReservationModelListEventSticky : get pending reservation sticky result
     */
    public static class getPendingReservationModelListEventSticky extends BaseStickyEvent {

        public List<BookingModel> reservationModelList;
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;

        public getPendingReservationModelListEventSticky(boolean isSuccess, List<BookingModel> reservationModelList) {
            this.isSuccess = isSuccess;
            this.reservationModelList = reservationModelList;
        }

        public getPendingReservationModelListEventSticky(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public static class GetCurrentBookingCount extends BaseStickyEvent {
        public final boolean isSuccess;
        public int bookingCount;
        public String errorMessage;
        public int errorCode;

        public GetCurrentBookingCount(boolean isSuccess, int count) {
            this.isSuccess = isSuccess;
            this.bookingCount = count;
        }

        public GetCurrentBookingCount(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    /* Get Total Earning event status */
    public static class GetTotalEarningEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public double totalEarning;
        public String errorMessage;
        public int errorCode;

        public GetTotalEarningEventStatus(boolean isSuccess, double totalEarning) {
            this.isSuccess = isSuccess;
            this.totalEarning = totalEarning;
        }

        public GetTotalEarningEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    /* Get Total Earning event status */
    public static class getReservedModelInfoEventStatus extends BaseStickyEvent{
        public final boolean isSuccess;
        public BookingModel booking;
        public String errorMessage;
        public int errorCode;

        public getReservedModelInfoEventStatus(boolean isSuccess, BookingModel bookingModel) {
            this.isSuccess = isSuccess;
            this.booking = bookingModel;
        }

        public getReservedModelInfoEventStatus(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    /* Set Availability status event */
    public static class SetAvailableStatusEventStatus extends BaseStickyEvent {
        public final boolean isSuccess;
        public int isAvailable;
        public boolean isSilentLogin;
        public String errorMessage;
        public int errorCode;

        public SetAvailableStatusEventStatus(boolean isSuccess , int isAvailable , boolean isSilentLogin) {
            this.isSuccess = isSuccess;
            this.isAvailable = isAvailable;
            this.isSilentLogin = isSilentLogin;
        }

        public SetAvailableStatusEventStatus(boolean isSuccess , int errorCode , String error) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    /* Set Availability status event */
    public static class SetAllPhotosListEventStatus extends BaseStickyEvent {
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;
        public List<ResponseViews.Photos> photosList;

        public SetAllPhotosListEventStatus(boolean isSuccess, List<ResponseViews.Photos> photosList) {
            this.isSuccess = isSuccess;
            this.photosList = photosList;
        }

        public SetAllPhotosListEventStatus(boolean isSuccess , int errorCode , String error) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    /* Set New Photo event */
    public static class SetNewPhotoEventStatus extends BaseStickyEvent {
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;
        public ResponseViews.GetNewPhotosResponse newPhoto;

        public SetNewPhotoEventStatus(boolean isSuccess, ResponseViews.GetNewPhotosResponse newPhoto) {
            this.isSuccess = isSuccess;
            this.newPhoto = newPhoto;
        }

        public SetNewPhotoEventStatus(boolean isSuccess , int errorCode , String error) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    /* Set Remove Photo event */
    public static class SetRemovePhotoEventStatus extends BaseStickyEvent {
        public final boolean isSuccess;
        public String errorMessage;
        public int errorCode;
        public int id;

        public SetRemovePhotoEventStatus(boolean isSuccess, int id) {
            this.isSuccess = isSuccess;
            this.id = id;
        }

        public SetRemovePhotoEventStatus(boolean isSuccess , int errorCode , String error) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = error;
        }
    }

    public static class GCMEventStickyResult extends BaseStickyEvent{
        public final int bookingId;
        public final String message;
        public final int status;

        public GCMEventStickyResult(int bookingId, String message, int status) {
            this.bookingId = bookingId;
            this.message = message;
            this.status = status;
        }
    }

    /**
     * Twilio Call status event
     */
    public static class TwilioCallStatusEvent extends BaseStickyEvent {
        public final boolean isSuccess;
        public int errorCode;
        public String errorMessage;

        public TwilioCallStatusEvent(boolean isSuccess, int errorCode, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public static class UpdateReservationScreenEventStatus extends BaseStickyEvent{
        private final int bookingId;
        private final int status;

        public UpdateReservationScreenEventStatus(int bookingId, int status) {
            this.bookingId = bookingId;
            this.status = status;
        }

        public int getBookingId() {
            return bookingId;
        }

        public int getStatus() {
            return status;
        }
    }
}
