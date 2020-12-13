package com.example.msplug.retrofit.endpoints.endpoint_request_detail;

import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface apirequestdetail {
    @Headers({
            "Content-Type:application/json",
            //come back and put token
            "Authorization:Token 706d7cb935c7fe41c648c377988108c3acfad136"
    })
    @PATCH("request-detail/{requestID}")
    Call<requestlistresponse> updateRequestDetails(@Path("requestID") int requestID, @Body requestdetailsbody requestdetailsbody);
}
