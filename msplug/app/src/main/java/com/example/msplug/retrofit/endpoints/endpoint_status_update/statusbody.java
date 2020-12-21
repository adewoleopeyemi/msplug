package com.example.msplug.retrofit.endpoints.endpoint_status_update;

import com.google.gson.annotations.SerializedName;

public class statusbody {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
