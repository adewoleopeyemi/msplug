package com.example.msplug.retrofit.endpoints.endpoint_user_request_list;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class userrequestlistbody {
    @SerializedName("")
    private List<request> requestLists;


    public List<request> getRequestLists() {
        return requestLists;
    }
}
