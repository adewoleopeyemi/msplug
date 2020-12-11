package com.example.msplug.retrofit.endpoint_request_list;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface apirequestlist {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @GET("request-list/{deviceID}")
    Call<requestlistresponse> getRequestList(@Path("deviceID") String deviceID);
}
