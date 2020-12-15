package com.example.msplug.retrofit.endpoints.endpoint_user_request_list;

import com.google.gson.annotations.SerializedName;

public class body {
    @SerializedName("device_ID")
    private String deviceID;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
