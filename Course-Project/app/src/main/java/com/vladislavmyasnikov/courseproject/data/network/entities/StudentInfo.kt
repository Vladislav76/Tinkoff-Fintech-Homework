package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class Student(
        @SerializedName("student_id") val id: Int,
        @SerializedName("student") val name: String,
        @SerializedName("grades") val grades: List<Mark>
)

class Mark(@SerializedName("mark") val mark: Double)



