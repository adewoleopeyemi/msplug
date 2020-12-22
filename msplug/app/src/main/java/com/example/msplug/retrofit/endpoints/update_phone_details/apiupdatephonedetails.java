package com.example.msplug.retrofit.endpoints.update_phone_details;

import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusresponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apiupdatephonedetails {
    @PUT("device-update/{id}")
    Call<statusresponse> updatephone(@HeaderMap Map<String, String> headers, @Path("id") String id, @Body updatebody body);
}
