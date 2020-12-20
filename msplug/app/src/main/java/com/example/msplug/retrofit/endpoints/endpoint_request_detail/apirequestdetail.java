package com.example.msplug.retrofit.endpoints.endpoint_request_detail;

import android.content.Context;

import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface apirequestdetail {

    /**@Headers({
            "Content-Type:application/json",
            //come back and put token
            token
    })**/
    @PATCH("request-detail/{requestID}")
    Call<requestlistresponse> updateRequestDetails(@HeaderMap Map<String, String> headers, @Path("requestID") int requestID, @Body requestdetailsbody requestdetailsbody);
}
