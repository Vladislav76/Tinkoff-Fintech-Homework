package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class ProfileJson(
        @SerializedName("id") val id: Int,
        @SerializedName("birthday") val birthday: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("first_name") val firstName: String?,
        @SerializedName("last_name") val lastName: String?,
        @SerializedName("middle_name") val middleName: String?,
        @SerializedName("phone_mobile") val phoneMobile: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("region") val region: String?,
        @SerializedName("faculty") val faculty: String?,
        @SerializedName("department") val department: String?,
        @SerializedName("avatar") val avatarUrl: String?,
        @SerializedName("university") val university: String?
)