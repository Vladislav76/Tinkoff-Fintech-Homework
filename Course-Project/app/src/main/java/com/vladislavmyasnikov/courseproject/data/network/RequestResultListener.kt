package com.vladislavmyasnikov.courseproject.data.network

import retrofit2.Call
import retrofit2.Response

interface RequestResultListener<T> {

    fun onFailure(call: Call<T>, e: Throwable)
    fun onResponse(call: Call<T>, response: Response<T>)
}
