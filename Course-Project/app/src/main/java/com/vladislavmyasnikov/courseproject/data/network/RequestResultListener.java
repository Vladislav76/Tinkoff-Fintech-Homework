package com.vladislavmyasnikov.courseproject.data.network;

import retrofit2.Call;
import retrofit2.Response;

public interface RequestResultListener<T> {

    void onFailure(Call<T> call, Throwable e);
    void onResponse(Call<T> call, Response<T> response);
}
