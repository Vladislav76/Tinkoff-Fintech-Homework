package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class StudentJson(
        @SerializedName("student_id") val id: Int,
        @SerializedName("student") val name: String,
        @SerializedName("grades") val grades: List<MarkJson>
)

class MarkJson(
        @SerializedName("id") val id: Int = 0,
        @SerializedName("mark") val mark: Float = 0.0f,
        @SerializedName("status") val status: String = "",
        @SerializedName("task_type") val taskType: String = ""
)



