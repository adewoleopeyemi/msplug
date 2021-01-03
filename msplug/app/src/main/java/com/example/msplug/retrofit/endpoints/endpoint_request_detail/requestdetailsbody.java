package com.example.msplug.retrofit.endpoints.endpoint_request_detail;

import com.google.gson.annotations.SerializedName;

public class requestdetailsbody {
    @SerializedName("response_message")
    private String response_message;
    @SerializedName("status")
    private String status;
    @SerializedName("device")
    private String device;

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
