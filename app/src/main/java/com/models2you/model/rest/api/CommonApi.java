package com.models2you.model.rest.api;

import com.models2you.model.rest.models.ResponseViews;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by yogeshsoni on 26/09/16.
 * Common api to generate request body
 */

public interface CommonApi {

    /*@Multipart
    @POST("/provider/new_register")
    Call<ResponseViews.SignUpResponse> doSignUp(@Part("picture\"; filename=\"picture.png\" ") RequestBody file1, @Part("picture1\"; filename=\"picture1.png\" ") RequestBody file2, @Part("picture2\"; filename=\"picture2.png\" ") RequestBody file3,
                                                @Part("picture3\"; filename=\"picture3.png\" ") RequestBody file4, @Part("w9_form\"; filename=\"w9_form.pdf\" ") RequestBody file5,
                                                @Query("email") String email, @Query("password") String password, @Query("name") String name, @Query("phone") String phone, @Query("address") String address, @Query("state") String state,
                                                @Query("city") String city, @Query("instagram") String instagram, @Query("facebook") String facebook, @Query("favorites") String favorites, @Query("eyecolor") String eyecolor,
                                                @Query("haircolor") String haircolor, @Query("height_foot") int height_foot, @Query("height_inch") int height_inch, @Query("rate") int rate, @Query("zipcode") String zipcode, @Query("dob") String birthday);*/

    @Multipart
    @POST("/provider/new_register")
    Call<ResponseViews.SignUpResponse> doSignUp(@Part("picture\"; filename=\"picture.png\" ") RequestBody file1, @Part("picture1\"; filename=\"picture1.png\" ") RequestBody file2, @Part("picture2\"; filename=\"picture2.png\" ") RequestBody file3,
                                                @Part("picture3\"; filename=\"picture3.png\" ") RequestBody file4,
                                                @Query("email") String email, @Query("password") String password, @Query("name") String name, @Query("phone") String phone, @Query("address") String address, @Query("state") String state,
                                                @Query("city") String city, @Query("instagram") String instagram, @Query("facebook") String facebook, @Query("favorites") String favorites, @Query("eyecolor") String eyecolor,
                                                @Query("haircolor") String haircolor, @Query("height_foot") int height_foot, @Query("height_inch") int height_inch, @Query("rate") int rate, @Query("zipcode") String zipcode, @Query("dob") String birthday);

    /* ------ Update status of current booking For :- Accept , Deny , Cancel -------*/
    @POST("/booking/set_status")
    Call<ResponseViews.UpdateBookingStatusResponse> updateBookingStatusOfCurrentBooking(@Query("id") int ownerId, @Query("token") String token, @Query("booking_id") long bookingId, @Query("status") int statusVal,@Query("reason") String message, @Query("who") int who);

    /* ----- Update status of Current Booking for :-  postponed -------------*/
    @POST("/booking/set_notify_status")
    Call<ResponseViews.UpdateBookingStatusResponse> delayStatusOfCurrentBooking(@Query("id") int ownerId, @Query("token") String token, @Query("booking_id") long bookingId,
                                                                                @Query("notify_status") int statusVal, @Query("who") int who, @Query("delay") int delayInMinutes);

    /* ----- Update status of Current Booking for :-  on my way , arrived -------------*/
    @POST("/booking/set_notify_status")
    Call<ResponseViews.UpdateBookingStatusResponse> updateNotifyStatusOfCurrentBooking(@Query("id") int ownerId, @Query("token") String token, @Query("booking_id") long bookingId,
                                                                                        @Query("notify_status") int statusVal, @Query("who") int who);

    @POST("/booking/list_by_status")
    Call<ResponseViews.CurrentAndPreviousReservationResponse> getCurrentAndPreviousReservation(@Query("token") String token, @Query("id") int ownerId, @Query("status[]") List<Integer> status);

    @POST("/provider/new_login")
    Call<ResponseViews.LoginDataResponse> generateLoginData(@Query("email") String email , @Query("password") String password, @Query("device_type") String deviceType);

//    @POST("/provider/update_latitude")
//    Call<ResponseViews.UpdateLatLongResponse> updateLatLong(@Query("latitude") String latitude , @Query("longitude") String longitude, @Query("token") String token,@Query("id") String id);

    @POST("/provider/update_device_token")
    Call<ResponseViews.GCMRegistrationResponse> registerGcmToken(@Body com.models2you.model.rest.models.view.RequestBody.GCMTokenRequestView requestView);

    @POST("/application/forgot-password")
    Call<ResponseViews.ForgetPasswordResponse> forgetPassword(@Query("email") String email , @Query("type") int type);

    @POST("/application/change-password")
    Call<ResponseViews.ChangePasswordResponse> changePassword(@Query("new_password") String newPassword ,@Query("id") int id , @Query("token") String token);

    @POST("/provider/update_device_token")
    Call<ResponseViews.LogoutResponse> doLogout(@Query("token") String token , @Query("id") int ownerId , @Query("device_token") String deviceToken , @Query("device_type") String deviceType);

    @POST("/booking/count")
    Call<ResponseViews.GetCurrentBookingCountResponse> getCurrentBookingCount(@Query("id") int ownerId , @Query("token") String token ,@Query("status[]") List<Integer> bookingType, @Query("current_time") String currentTime);

    @POST("/provider/get_total_earnings")
    Call<ResponseViews.getTotalEarningResponse> getTotalEarning(@Query("id") int ownerId , @Query("token") String token);

    @POST("/booking/info")
    Call<ResponseViews.getReservedModelInfoResponse> getReservedModelInfo(@Query("id") int ownerId , @Query("token") String token, @Query("booking_id") int bookingId,@Query("user_type") int user_type);

    @POST("/provider/new_set_availability")
    Call<ResponseViews.SetAvailabilityStatusResponse> setAvailabilityStatus(@Body com.models2you.model.rest.models.view.RequestBody.SendAvailabilityStatusRequestBody sendAvailabilityStatusRequestBody);

    @POST("/provider/get_all_photos")
    Call<ResponseViews.GetAllPhotosResponse> getAllPhotos(@Query("token") String token, @Query("model_id") int modelId);

    @POST("/provider/remove_photo")
    Call<ResponseViews.GetRemovePhotoResponse> removePhotos(@Query("token") String token, @Query("id") int modelId, @Query("photo_id") int photoId);

    @Multipart
    @POST("/provider/new_update")
    Call<ResponseViews.UpdateUserProfileResponse> updateUserProfile(@PartMap Map<String, RequestBody> requestBodyMap);

    @Multipart
    @POST("/provider/add_photo")
    Call<ResponseViews.GetNewPhotosResponse> addNewPhoto(@Part("picture\"; filename=\"picture.png\" ") RequestBody file , @Query("token") String token , @Query("id") int ownerId );

    @POST("/provider/renew_token_expiry")
    Call<ResponseViews.RenewTokenResponse> renewTokenExpiry(@Query("id") int ownerId , @Query("token") String token);

    @POST("/provider/new_update_location")
    Call<ResponseViews.UpdateLocationResponseView> updateCurrentLocation(@Query("id") int id , @Query("token") String token, @Query("lat") String lat, @Query("lon") String lon);

    //Twilio call API
    @POST("/user/call_user")
    Call<ResponseViews.TwilioCallResponseView> getTwilioCallResponse(@Query("token") String token , @Query("id") int userId,
                                                                     @Query("calleePhone") long calleePhone, @Query("callerPhone") long callerPhone);
}
