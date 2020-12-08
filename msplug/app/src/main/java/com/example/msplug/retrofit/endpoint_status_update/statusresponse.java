package com.example.msplug.retrofit.endpoint_status_update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class statusresponse {
    @SerializedName("id")
    private transient int id;
    @SerializedName("deviceID")
    private String deviceID;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private String status;
    @SerializedName("start_date")
    private String start_date;
    @SerializedName("expire_date")
    private String expire_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }
}
