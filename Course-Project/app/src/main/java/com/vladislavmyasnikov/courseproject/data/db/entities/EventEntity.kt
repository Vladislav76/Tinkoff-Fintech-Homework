package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "events")
data class EventEntity(
        @PrimaryKey val title: String,
        @ColumnInfo(name = "is_actual") val isActual: Boolean,
        @ColumnInfo(name = "date_start") val startDate: Date?,
        @ColumnInfo(name = "date_end") val endDate: Date?,
        @ColumnInfo(name = "event_type_name") val eventTypeName: String?,
        @ColumnInfo(name = "event_type_color") val eventTypeColor: String?,
        val place: String,
        val description: String
)