package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class TaskInfo {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("mark")
    var mark: Double = 0.toDouble()

    @SerializedName("task")
    var task: Task? = null
}
