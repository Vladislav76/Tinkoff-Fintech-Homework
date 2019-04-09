package com.vladislavmyasnikov.courseproject.data.network

import android.os.SystemClock

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor() {

    private val mRetrofit: Retrofit
    val fintechService: FintechService

    init {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    SystemClock.sleep(2000)
                    chain.proceed(original)
                }.build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

        fintechService = mRetrofit.create(FintechService::class.java)
    }



    companion object {

        private const val BASE_URL = "https://fintech.tinkoff.ru/api/"
        private var sInstance: NetworkService? = null

        fun getInstance(): NetworkService =
                sInstance ?: synchronized(NetworkService::class.java) { sInstance ?: NetworkService().also { sInstance = it }}
    }
}
