package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Result {

    @SerializedName("homeworks")
    val lectures: List<Lecture>? = null

    @SerializedName("user")
    val user: User? = null

    @SerializedName("grades")
    val students: List<Student>? = null
}
