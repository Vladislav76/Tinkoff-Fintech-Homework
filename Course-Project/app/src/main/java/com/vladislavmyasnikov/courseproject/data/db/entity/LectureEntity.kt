package com.vladislavmyasnikov.courseproject.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.data.models.Identifiable

@Entity(tableName = "lectures")
data class LectureEntity(@PrimaryKey override val id: Int,
                         val title: String) : Identifiable
