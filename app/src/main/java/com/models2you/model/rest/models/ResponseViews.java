package com.models2you.model.rest.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Amit on 9/28/2016.
 * Generic Response View classes (POJOs) for REST responses
 */
public class ResponseViews {

    public static class BaseResponseView {
        @SerializedName("success")
        public boolean isSuccess;
        @SerializedName("error")
        public String errorMessage;
        @SerializedName("error_code")
        public int errorCode;
    }

    public static class UpdateBookingStatusResponse extends BaseResponseView{
    }

    public static class CurrentAndPreviousReservationResponse extends BaseResponseView{
        public List<Booking> bookings;

        public String toString() {
            return "ClassPojo [error = \"+error+\", error_code = \"+error_code+\" ,error_code = "+errorCode+",success = "+isSuccess+",bookings = "+bookings+"]";
        }
    }

    /* Booking response used in viewCartResponse , getPreviousResponse , getCurrentResponse */
    // Note : don't delete
    public static class Booking {
        @SerializedName("id")
        public int id;
        @SerializedName("client_id")
        public long clientId;
        @SerializedName("status")
        public int status;
        @SerializedName("duration")
        public int duration;
        @SerializedName("rate")
        public float rate;
        @SerializedName("location")
        public String location;
        @SerializedName("comment")
        public String comment;
        @SerializedName("wear")
        public String wear;
        @SerializedName("appointment_datetime")
        public String appointmentTime;
        @SerializedName("model_id")
        public int modelId;
        @SerializedName("book_date")
        public String bookDate;
        @SerializedName("name")
        public String name;
        @SerializedName("picture")
        public String pictureUrl;
        @SerializedName("arrived_time")
        public String arrivedTime;
        @SerializedName("modified_time")
        public String modifiedTime;
        @SerializedName("transaction_id")
        public String transactionId;
        @SerializedName("who")
        public int who;
        @SerializedName("reason")
        public String reason;
        @SerializedName("paid")
        public int paid;
        @SerializedName("notify_status")
        public int notifyStatus;
        @SerializedName("delayed")
        public int delayed;
        @SerializedName("delay")
        public int delay;
        @SerializedName("phone")
        public String phone;
        //this tag is using in Current reservation model
        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
        @SerializedName("fee_percent")
        public float feePercent;
        @SerializedName("booking_fee_per_hour")
        public float bookingFeePerHour;
        @SerializedName("model_rate_per_hour")
        public float modelRatePerHour;
        @SerializedName("calculatedRate")
        public float calculatedRate;
        @SerializedName("book_now")
        public int bookNow;
    }

    public static class LoginDataResponse extends BaseResponseView{
        public long phone;
        @SerializedName("session_timeout")
        public long loginExpireTime;
        public String zipcode;
        public String state;
        public String city;
        public int id;
        public String picture;
        public String token;
        public String address;
        public String name;
        public String dob;
        public String haircolor;
        public String eyecolor;
        public String height_foot;
        public String height_inch;
        public String favorites;
        public String rate;
        public String facebook;
        public String instagram;
        public int availability;

        public String toString() {
            return "ClassPojo [error = \"+error+\", error_code = \"+error_code+\",phone = "+phone+", favorites = "+favorites+", zipcode = "+zipcode+", state = "+state+", city = "+city+", id = "+id+", picture = "+picture+", token = "+token+", address = "+address+", name = "+name+", dob = "+dob+", success = "+isSuccess+"]";
        }
    }

    /* Sign Up Response to handle response from SignUp Request */
    public static class SignUpResponse extends BaseResponseView{
        public String picture;
        public int id;
        public String token;

        @Override
        public String toString() {
            return "ClassPojo [picture = "+picture+", id = "+id+", token = "+token+", success = "+isSuccess+"]";
        }
    }
    /* change password response */
    public static class ChangePasswordResponse extends BaseResponseView{
    }

    /* ForgetPasswordResponse response handling */
    public static class ForgetPasswordResponse extends BaseResponseView{
    }
    /* GCM Registration success/failure result */
    public static class GCMRegistrationResponse extends BaseResponseView{
    }

    /* GCM Registration success/failure result */
    public static class UpdateLatLongResponse extends BaseResponseView{
    }

    /* Logout response */
    public static class LogoutResponse extends BaseResponseView{
    }

    /* Get current Booking Count */
    public static class GetCurrentBookingCountResponse extends BaseResponseView{
        @SerializedName("count")
        public int count;
    }

    /* Get total earning response */
    public static class getTotalEarningResponse extends BaseResponseView{
        @SerializedName("total_earnings")
        public double totalEarning;
    }

    /* set Availability status response */
    public static class SetAvailabilityStatusResponse extends BaseResponseView{
    }

    /* get Reserved model info response */
    public static class getReservedModelInfoResponse extends BaseResponseView {
        @SerializedName("booking")
        public Booking booking;
    }

    /* set Availability status response */
    public static class GetAllPhotosResponse extends BaseResponseView{
        @SerializedName("photos")
        public List<Photos> photosList;
    }

    public static class Photos {
        @SerializedName("id")
        public int id;
        @SerializedName("model_id")
        public int modelId;
        @SerializedName("url")
        public String photoUrl;
    }

    /* set Availability status response */
    public static class GetNewPhotosResponse extends BaseResponseView{
        @SerializedName("id")
        public int id;
        @SerializedName("model_id")
        public int modelId;
        @SerializedName("url")
        public String photoUrl;
    }
    /* Update User profile Response */
    public static class UpdateUserProfileResponse extends BaseResponseView{
    }

    /* set remove photos response*/
    public static class GetRemovePhotoResponse {
        @SerializedName("success")
        public boolean isSuccess;
        @SerializedName("error")
        public String errorMessage;
        @SerializedName("error_code")
        public int errorCode;

        public int photoId;
    }

    public static class RenewTokenResponse {
        @SerializedName("success")
        public boolean isSuccess;
        @SerializedName("error")
        public String errorMessage;
        @SerializedName("error_code")
        public int errorCode;
    }

    public class UpdateLocationResponseView {
        @SerializedName("success")
        public boolean isSuccess;
        @SerializedName("error")
        public String errorMessage;
        @SerializedName("error_code")
        public int errorCode;
    }

    public class TwilioCallResponseView {
        @SerializedName("success")
        public boolean isSuccess;
        @SerializedName("error")
        public String errorMessage;
        @SerializedName("error_code")
        public int errorCode;
    }
}
