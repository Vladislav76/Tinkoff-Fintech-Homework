package com.vladislavmyasnikov.courseproject.data.models

import com.google.gson.annotations.SerializedName

class Login(@field:SerializedName("email")
            private val mEmail: String, @field:SerializedName("password")
            private val mPassword: String)
