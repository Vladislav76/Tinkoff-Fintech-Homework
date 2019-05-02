package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.domain.models.Identifiable
import java.util.*

@Entity(tableName = "tasks",
        foreignKeys = [ForeignKey(entity = LectureEntity::class, parentColumns = ["id"], childColumns = ["lecture_id"], onDelete = CASCADE)])
data class TaskEntity(
        @PrimaryKey val id: Int,
        val title: String,
        val status: String,
        val mark: Double,
        @ColumnInfo(name = "deadline_date") val deadline: Date?,
        @ColumnInfo(name = "max_score") val maxScore: Double,
        @ColumnInfo(name = "lecture_id", index = true) val lectureId: Int
)