package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

@Entity(tableName = "lectures")
data class LectureEntity(
        @PrimaryKey val id: Int,
        val title: String
)
