package com.vladislavmyasnikov.courseproject.data.network

import com.vladislavmyasnikov.courseproject.data.models.Login
import com.vladislavmyasnikov.courseproject.data.models.Result

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FintechService {

    @POST("signin")
    fun getAccess(@Body login: Login): Call<Void>

    @GET("user")
    fun getUser(@Header("Cookie") token: String): Call<Result>

    @GET("course/android_spring_2019/homeworks")
    fun getLectures(@Header("Cookie") token: String): Call<Result>

    @GET("course/android_spring_2019/grades")
    fun getStudents(@Header("Cookie") token: String): Call<List<Result>>
}
