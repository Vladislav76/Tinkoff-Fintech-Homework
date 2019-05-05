package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
        @PrimaryKey val id: Int,
        val name: String = "",
        val mark: Float = 0.0f
)