package com.example.msplug.retrofit.endpoint_status_update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class statusbody {
    @SerializedName("status")
    private transient String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
