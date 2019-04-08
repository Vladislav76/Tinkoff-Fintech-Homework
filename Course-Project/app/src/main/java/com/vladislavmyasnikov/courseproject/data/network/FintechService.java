package com.vladislavmyasnikov.courseproject.data.network;

import com.vladislavmyasnikov.courseproject.data.models.Login;
import com.vladislavmyasnikov.courseproject.data.models.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FintechService {

    @POST("signin")
    Call<Void> getAccess(@Body Login login);

    @GET("user")
    Call<Result> getUser(@Header("Cookie") String token);

    @GET("course/android_spring_2019/homeworks")
    Call<Result> getLectures(@Header("Cookie") String token);
}
