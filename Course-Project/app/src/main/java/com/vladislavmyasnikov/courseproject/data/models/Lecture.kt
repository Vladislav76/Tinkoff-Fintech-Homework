package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Lecture {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("tasks")
    var tasks: List<TaskInfo>? = null
}
