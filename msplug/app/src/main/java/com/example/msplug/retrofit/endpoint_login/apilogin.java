package com.example.msplug.retrofit.endpoint_login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface apilogin {
    @POST("login/")
    @FormUrlEncoded
    Call<loginresponse> verifyUser(@Field("email") String email,
                                   @Field("password") String password,
                                   @Field("deviceID") String deviceID);
}
