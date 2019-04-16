package com.vladislavmyasnikov.courseproject.data.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestResultCallback<T>(var listener: RequestResultListener<T>?) : Callback<T> {

    override fun onFailure(call: Call<T>, e: Throwable) {
        listener?.onFailure(call, e)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        listener?.onResponse(call, response)
    }
}
