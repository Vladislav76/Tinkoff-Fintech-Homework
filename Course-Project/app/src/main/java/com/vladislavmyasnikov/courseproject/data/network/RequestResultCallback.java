package com.vladislavmyasnikov.courseproject.data.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestResultCallback<T> implements Callback<T> {

    private RequestResultListener<T> mRequestResultListener;

    public RequestResultCallback(RequestResultListener<T> listener) {
        mRequestResultListener = listener;
    }

    public void setRequestResultListener(RequestResultListener<T> listener) {
        mRequestResultListener = listener;
    }

    @Override
    public void onFailure(Call<T> call, Throwable e) {
        if (mRequestResultListener != null) {
            mRequestResultListener.onFailure(call, e);
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (mRequestResultListener != null) {
            mRequestResultListener.onResponse(call, response);
        }
    }
}
