package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Course(@SerializedName("title") val title: String,
             @SerializedName("url") val url: String)

