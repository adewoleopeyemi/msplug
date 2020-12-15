package com.example.msplug.retrofit.endpoints.endpoint_user_request_list;

import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface apiuserrequestlistbody {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @GET("user-request-list/")
    @FormUrlEncoded
    Call<requestlistresponse> getRequestList(@Field("deviceID") String deviceID);
}
