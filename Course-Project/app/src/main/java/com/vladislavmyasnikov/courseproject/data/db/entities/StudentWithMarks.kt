package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

class StudentWithMarks {

    @Embedded
    lateinit var student: StudentEntity

    @Relation(entity = MarkEntity::class, parentColumn = "id", entityColumn = "student_id")
    lateinit var marks: List<MarkEntity>
}