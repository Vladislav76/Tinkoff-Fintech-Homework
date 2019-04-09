package com.vladislavmyasnikov.courseproject.data.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestResultCallback<T>(private var mRequestResultListener: RequestResultListener<T>?) : Callback<T> {

    fun setRequestResultListener(listener: RequestResultListener<T>?) {
        mRequestResultListener = listener
    }

    override fun onFailure(call: Call<T>, e: Throwable) {
        if (mRequestResultListener != null) {
            mRequestResultListener!!.onFailure(call, e)
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (mRequestResultListener != null) {
            mRequestResultListener!!.onResponse(call, response)
        }
    }
}
