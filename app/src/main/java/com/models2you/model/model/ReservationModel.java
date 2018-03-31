package com.models2you.model.model;



import com.models2you.model.rest.models.ResponseViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amit on 9/1/2016.
 * Reservation model class for Previous Reservation and Current Reservation Screen
 */
public class ReservationModel {


    private final boolean isSuccess;
    private final List<ResponseViews.Booking> bookingList;
    private List<BookingModel> reservationModelList;

    public ReservationModel(boolean isSuccess, List<ResponseViews.Booking> bookings) {
        this.isSuccess = isSuccess;
        this.bookingList = bookings;
        fillReservationDataModel(bookingList);
    }

    /**
     * getReservationModelList : method to return reservation model list
     * @return list<BookingModel>
     */
    public List<BookingModel> getReservationModelList() {
        return reservationModelList;
    }

    /**
     * fillReservationDataModel : method to set Booking Model List
     * @param bookingList : list of booking response
     */
    private void fillReservationDataModel(List<ResponseViews.Booking> bookingList) {
        reservationModelList = new ArrayList<>();
        for (ResponseViews.Booking bookingResponse : bookingList) {
            BookingModel bookingModel = new BookingModel();
            bookingModel.id = bookingResponse.id;
            bookingModel.clientId = bookingResponse.clientId;
            bookingModel.modelId = bookingResponse.modelId;
            bookingModel.status = bookingResponse.status;
            bookingModel.bookDate = bookingResponse.bookDate;
            bookingModel.appointmentTime = bookingResponse.appointmentTime;
            bookingModel.duration = bookingResponse.duration;
            bookingModel.rate = bookingResponse.rate;
            bookingModel.location = bookingResponse.location;
            bookingModel.comment = bookingResponse.comment;
            bookingModel.wear =  bookingResponse.wear ;
            bookingModel.name = bookingResponse.name;
            bookingModel.pictureUrl = bookingResponse.pictureUrl;
            bookingModel.paid = bookingResponse.paid;
            bookingModel.notifyStatus = bookingResponse.notifyStatus;
            bookingModel.reason = bookingResponse.reason;
            bookingModel.who = bookingResponse.who;
            bookingModel.modifiedTime = bookingResponse.modifiedTime;
            bookingModel.transactionId = bookingResponse.transactionId;
            bookingModel.delayed = bookingResponse.delayed;
            bookingModel.arrivedTime = bookingResponse.arrivedTime;
            bookingModel.delay = bookingResponse.delay;
            bookingModel.phone = bookingResponse.phone;
            bookingModel.latitude = bookingResponse.latitude;
            bookingModel.longitude = bookingResponse.longitude;
            reservationModelList.add(bookingModel);
        }
    }
}
