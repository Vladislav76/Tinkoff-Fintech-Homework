package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Profile(@SerializedName("first_name") val firstName: String,
              @SerializedName("last_name") val lastName: String,
              @SerializedName("middle_name") val middleName: String,
              @SerializedName("avatar") val avatarUrl: String)