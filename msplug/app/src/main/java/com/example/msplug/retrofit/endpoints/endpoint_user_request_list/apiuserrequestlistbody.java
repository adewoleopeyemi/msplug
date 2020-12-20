package com.example.msplug.retrofit.endpoints.endpoint_user_request_list;

import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface apiuserrequestlistbody {
    @GET("user-request-list")
    Call<List<requestlistresponse>> getRequestList(@HeaderMap Map<String, String> headers, @Query("device_ID") String deviceID);
}
