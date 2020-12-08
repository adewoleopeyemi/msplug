package com.example.msplug.retrofit.endpoint_login;

import com.example.msplug.retrofit.endpoint_status_update.statusbody;
import com.example.msplug.retrofit.endpoint_status_update.statusresponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.example.msplug.utils.sharedPrefences.Constants.KEY_TOKEN;

public interface apistatusupdate {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @PUT("device-update/{id}")
    Call<statusresponse> updateStatus(@Path("id") String id, @Body statusbody body);
}
