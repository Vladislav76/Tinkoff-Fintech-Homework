package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Student {

    @SerializedName("student_id")
    val id: Int = 0

    @SerializedName("student")
    val name: String = ""

    @SerializedName("grades")
    val grades: List<Mark>? = null



    class Mark {

        @SerializedName("mark")
        val mark: Double = 0.0
    }
}
