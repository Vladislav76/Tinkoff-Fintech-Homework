package com.vladislavmyasnikov.courseproject.data.network

import com.google.gson.annotations.SerializedName
import com.vladislavmyasnikov.courseproject.data.network.entities.CourseJson
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FintechPortalApi {

    @POST("signin")
    fun getAccess(@Body login: Login): Single<Response<Unit>>

    @GET("user")
    fun getProfile(@Header("Cookie") token: String): Single<ProfileInfo>

    @GET("connections")
    fun getCourses(@Header("Cookie") token: String): Single<CourseInfo>

    @GET("course/{course_url}/homeworks")
    fun getLectures(@Header("Cookie") token: String, @Path("course_url") courseUrl: String): Single<Lectures>

    @GET("course/{course_url}/grades")
    fun getStudents(@Header("Cookie") token: String, @Path("course_url") courseUrl: String): Single<List<Students>>
}


class Login(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String
)

class CookieData(val token: String, val time: String)

class Lectures(@SerializedName("homeworks") val lectures: List<LectureJson>)

class Students(@SerializedName("grades") val students: List<StudentJson>)

class ProfileInfo(@SerializedName("user") val profile: ProfileJson)

class CourseInfo(@SerializedName("courses") val courses: List<CourseJson>)