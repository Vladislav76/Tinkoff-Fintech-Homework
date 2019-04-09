package com.vladislavmyasnikov.courseproject.data.db.entity

import com.vladislavmyasnikov.courseproject.data.models.Identifiable

import java.util.Date

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "tasks",
        foreignKeys = [ForeignKey(entity = LectureEntity::class, parentColumns = ["id"], childColumns = ["lecture_id"], onDelete = CASCADE)])
data class TaskEntity(@PrimaryKey override val id: Int,
                      val title: String,
                      val status: String,
                      val mark: Double,
                      @ColumnInfo(name = "deadline_date") val deadline: Date?,
                      @ColumnInfo(name = "max_score") val maxScore: Double,
                      @ColumnInfo(name = "lecture_id", index = true) val lectureId: Int) : Identifiable