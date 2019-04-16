package com.vladislavmyasnikov.courseproject.data.network

import com.google.gson.annotations.SerializedName
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.Student
import com.vladislavmyasnikov.courseproject.data.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FintechService {

    @POST("signin")
    fun getAccess(@Body login: Login): Call<Void>

    @GET("user")
    fun getUser(@Header("Cookie") token: String): Call<UserInfo>

    @GET("course/android_spring_2019/homeworks")
    fun getLectures(@Header("Cookie") token: String): Call<Lectures>

    @GET("course/android_spring_2019/grades")
    fun getStudents(@Header("Cookie") token: String): Call<List<Students>>
}


class Login(@SerializedName("email") val email: String,
            @SerializedName("password") val password: String)

class Lectures(@SerializedName("homeworks") val lectures: List<Lecture>)

class Students(@SerializedName("grades") val students: List<Student>)

class UserInfo(@SerializedName("user") val user: User?)