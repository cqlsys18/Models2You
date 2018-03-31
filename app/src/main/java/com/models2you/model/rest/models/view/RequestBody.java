package com.models2you.model.rest.models.view;

import com.google.gson.annotations.SerializedName;

public class RequestBody {

    public static class SendAvailabilityStatusRequestBody {
        @SerializedName("id")
        public int ownerId;
        @SerializedName("token")
        public String token;
        @SerializedName("availability")
        public int isAvailable;
        @SerializedName("check_availability_time")
        public String checkAvailabilityTime;

        public SendAvailabilityStatusRequestBody(int ownerId, String token, int isAvailable, String checkAvailabilityTime) {
            this.ownerId = ownerId;
            this.token = token;
            this.isAvailable = isAvailable;
            this.checkAvailabilityTime = checkAvailabilityTime;
        }
    }

    public static class GCMTokenRequestView {
        @SerializedName("device_token")
        public String deviceToken;
        @SerializedName("id")
        public int ownerId;
        @SerializedName("token")
        public String token;
        @SerializedName("device_type")
        public String deviceType;

        public GCMTokenRequestView(int id , String token , String deviceToken, String deviceType) {
            this.ownerId = id;
            this.token = token;
            this.deviceToken = deviceToken;
            this.deviceType = deviceType;
        }
    }

    public static class UpdateProfileRequestBody {
        public final String name;
        public final String rate;
        public final String hairColor;
        public final String favorites;
        public final String eyeColor;
        public final int footSelectedVal;
        public final int inchSelectedVal;

        public UpdateProfileRequestBody(String name, String rate, String hairColor, String favorites, String eyeColor, int footSelectedVal, int inchSelectedVal) {
            this.name = name;
            this.rate = rate;
            this.hairColor = hairColor;
            this.favorites = favorites;
            this.eyeColor = eyeColor;
            this.footSelectedVal = footSelectedVal;
            this.inchSelectedVal = inchSelectedVal;
        }
    }
}