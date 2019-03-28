package com.vladislavmyasnikov.courseproject.ui.callbacks;

import com.vladislavmyasnikov.courseproject.models.Login;
import com.vladislavmyasnikov.courseproject.models.Result;
import com.vladislavmyasnikov.courseproject.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FintechService {

    @POST("signin")
    Call<Void> getAccess(@Body Login login);

    @GET("user")
    Call<Result> getUser(@Header("Cookie") String token);
}
