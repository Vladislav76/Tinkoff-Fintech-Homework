package com.vladislavmyasnikov.courseproject.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.data.models.Identifiable

@Entity(tableName = "students")
data class StudentEntity(@PrimaryKey override val id: Int,
                         val name: String = "",
                         val mark: Double = 0.0) : Identifiable