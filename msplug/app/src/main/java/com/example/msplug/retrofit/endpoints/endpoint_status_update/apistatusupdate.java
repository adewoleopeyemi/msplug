package com.example.msplug.retrofit.endpoints.endpoint_status_update;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apistatusupdate {
    @PUT("device-update/{id}")
    Call<statusresponse> updateStatus(@HeaderMap Map<String, String> headers, @Path("id") String id, @Body statusbody body);
}

