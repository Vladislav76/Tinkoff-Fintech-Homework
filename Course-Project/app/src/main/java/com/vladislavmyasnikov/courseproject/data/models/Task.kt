package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Task {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("max_score")
    var maxScore: Double = 0.toDouble()

    @SerializedName("deadline_date")
    var deadlineDate: String? = null
}
