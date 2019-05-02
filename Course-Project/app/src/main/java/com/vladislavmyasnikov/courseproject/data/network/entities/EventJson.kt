package com.vladislavmyasnikov.courseproject.data.network.entities

import com.google.gson.annotations.SerializedName

class EventJson(
        @SerializedName("title") val title: String,
        @SerializedName("date_start") val startDate: String,
        @SerializedName("date_end") val endDate: String,
        @SerializedName("event_type") val eventType: EventTypeJson?,
        @SerializedName("place") val place: String,
        @SerializedName("description") val description: String
)

class EventTypeJson(
        @SerializedName("name") val name: String,
        @SerializedName("color") val color: String
)