package com.example.msplug.retrofit.endpoint_login;

import com.google.gson.annotations.SerializedName;

public class loginresponse {
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Object data;
    @SerializedName("code")
    private String code;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}';
    }
}
