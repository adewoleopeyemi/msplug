package com.example.msplug.retrofit.endpoints.endpoint_request_list;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface apirequestlist {
    /**
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })**/
    @GET("request-list/{deviceID}")
    Call<body> getRequestList(@HeaderMap Map<String, String> headers, @Path("deviceID") String deviceID);
}
