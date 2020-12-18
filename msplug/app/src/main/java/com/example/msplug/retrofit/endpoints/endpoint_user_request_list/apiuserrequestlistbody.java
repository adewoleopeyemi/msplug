package com.example.msplug.retrofit.endpoints.endpoint_user_request_list;

import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface apiuserrequestlistbody {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @GET("user-request-list")
    Call<List<requestlistresponse>> getRequestList(@Query("device_ID") String deviceID);
}
