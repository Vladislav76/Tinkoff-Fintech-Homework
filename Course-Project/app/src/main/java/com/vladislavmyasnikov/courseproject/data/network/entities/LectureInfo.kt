package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class LectureJson(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("tasks") val tasks: List<TaskInfo>
)

class TaskInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("status") val status: String,
        @SerializedName("mark") val mark: Double,
        @SerializedName("task") val task: TaskJson?
)

class TaskJson(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("max_score") val maxScore: Double,
        @SerializedName("deadline_date") val deadlineDate: String
)