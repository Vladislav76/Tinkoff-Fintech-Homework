package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

@Entity(tableName = "marks",
        foreignKeys = [ForeignKey(entity = StudentEntity::class, parentColumns = ["id"], childColumns = ["student_id"], onDelete = CASCADE)])
data class MarkEntity(
        @PrimaryKey override val id: Int,
        val value: Float,
        val status: String,
        @ColumnInfo(name = "task_type") val taskType: String,
        @ColumnInfo(name = "student_id", index = true) val studentId: Int
) : Identifiable