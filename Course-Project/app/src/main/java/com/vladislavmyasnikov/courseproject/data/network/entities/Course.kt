package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class Course(
        @SerializedName("title") val title: String,
        @SerializedName("url") val url: String
)

