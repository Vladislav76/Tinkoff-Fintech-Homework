package com.vladislavmyasnikov.courseproject.data.network

import com.google.gson.annotations.SerializedName
import com.vladislavmyasnikov.courseproject.data.network.entities.CourseJson
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FintechPortalApi {

    @POST("signin")
    fun getAccess(@Body login: Login): Call<Unit>

    @GET("user")
    fun getProfile(@Header("Cookie") token: String): Call<ProfileInfo>

    @GET("connections")
    fun getCourses(@Header("Cookie") token: String): Call<CourseInfo>

    @GET("course/android_spring_2019/homeworks")
    fun getLectures(@Header("Cookie") token: String): Single<Lectures>

    @GET("course/android_spring_2019/grades")
    fun getStudents(@Header("Cookie") token: String): Call<List<Students>>
}


class Login(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String
)

class CookieData(val token: String, val time: String)

class Lectures(@SerializedName("homeworks") val lectures: List<LectureJson>)

class Students(@SerializedName("grades") val students: List<StudentJson>)

class ProfileInfo(@SerializedName("user") val profile: ProfileJson?)

class CourseInfo(@SerializedName("courses") val courses: List<CourseJson>)