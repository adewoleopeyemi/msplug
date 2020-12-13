package com.example.msplug.retrofit.endpoints.endpoint_login;

import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusbody;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusresponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apistatusupdate {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @PUT("device-update/{id}")
    Call<statusresponse> updateStatus(@Path("id") String id, @Body statusbody body);
}
