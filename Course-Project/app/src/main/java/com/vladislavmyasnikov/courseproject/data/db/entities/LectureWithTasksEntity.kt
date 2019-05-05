package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

class LectureWithTasksEntity(
        @Embedded val lecture: LectureEntity,
        @Relation(entity = TaskEntity::class, parentColumn = "id", entityColumn = "lecture_id") val tasks: List<TaskEntity>
)