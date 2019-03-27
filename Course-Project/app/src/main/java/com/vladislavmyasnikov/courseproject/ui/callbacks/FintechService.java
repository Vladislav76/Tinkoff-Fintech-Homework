package com.vladislavmyasnikov.courseproject.ui.callbacks;

import com.vladislavmyasnikov.courseproject.models.Login;
import com.vladislavmyasnikov.courseproject.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FintechService {

    @POST("signin")
    Call<User> getAccess(@Body Login login);
}
