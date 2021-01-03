package com.example.msplug.retrofit.endpoints.endpoint_request_list;

import com.google.gson.annotations.SerializedName;


public class body {
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private requestlistresponse data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public requestlistresponse getData() {
        return data;
    }

    public void setData(requestlistresponse data) {
        this.data = data;
    }
}
