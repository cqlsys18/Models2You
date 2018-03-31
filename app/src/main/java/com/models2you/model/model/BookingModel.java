package com.models2you.model.model;

import com.models2you.model.rest.models.ResponseViews;

import java.io.Serializable;

public class BookingModel implements Serializable{
        public int id;
        public long clientId;
        public int modelId;
        public int status;
        public String bookDate;
        public String appointmentTime;
        public int duration;
        public float rate;
        public String location;
        public String comment;
        public String wear;
        public int paid;
        public int notifyStatus;
        public String reason;
        public int who;
        public String modifiedTime;
        public String transactionId;
        public int delayed;
        public String arrivedTime;
        public int delay;
        public String name;
        public String pictureUrl;
        public String phone;
        public double latitude;
        public double longitude;
        public float feePercent;
        public float bookingFeePerHour;
        public float modelRatePerHour;
        public float calculatedRate;
        public int bookNow;
        /**
         * fillBookingDataModel : method to set Booking Model info
         */
        public static BookingModel fillBookingDataModel(ResponseViews.Booking booking) {
                BookingModel bookingModel = new BookingModel();
                bookingModel.id = booking.id;
                bookingModel.clientId = booking.clientId;
                bookingModel.modelId = booking.modelId;
                bookingModel.status = booking.status;
                bookingModel.bookDate = booking.bookDate;
                bookingModel.appointmentTime = booking.appointmentTime;
                bookingModel.duration = booking.duration;
                bookingModel.rate = booking.rate;
                bookingModel.location = booking.location;
                bookingModel.comment = booking.comment;
                bookingModel.wear = booking.wear;
                bookingModel.name = booking.name;
                bookingModel.pictureUrl = booking.pictureUrl;
                bookingModel.paid = booking.paid;
                bookingModel.notifyStatus = booking.notifyStatus;
                bookingModel.reason = booking.reason;
                bookingModel.who = booking.who;
                bookingModel.modifiedTime = booking.modifiedTime;
                bookingModel.transactionId = booking.transactionId;
                bookingModel.delayed = booking.delayed;
                bookingModel.arrivedTime = booking.arrivedTime;
                bookingModel.delay = booking.delay;
                bookingModel.phone = booking.phone;
                bookingModel.latitude = booking.latitude;
                bookingModel.longitude = booking.longitude;
                bookingModel.feePercent= booking.feePercent;
                bookingModel.bookingFeePerHour= booking.bookingFeePerHour;
                bookingModel.modelRatePerHour= booking.modelRatePerHour;
                bookingModel.calculatedRate= booking.calculatedRate;
                bookingModel.bookNow= booking.bookNow;
                return bookingModel;
        }

        /**
         * enum booking category
         */
        public enum BOOKING_CATEGORY{
                CURRENT,
                PENDING,
                PREVIOUS
        }
        /**
         * enum booking status
         */
        public enum BOOKING_STATUS {
                CREATED,
                BOOKED,
                ACCEPTED,
                DENIED,
                COMPLETED,
                CANCELED
        }
        /***
         * enum notify status
         */
        public enum NOTIFY_STATUS {
                NOT_LEFT,
                ON_MY_WAY,
                POSTPONED,
                ARRIVED
        }
        /***
         * enum Availability Status
         */
        public enum AVAILABILITY_STATUS {
                OFFLINE,
                ONLINE
        }

}