package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentWithMarks
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import com.vladislavmyasnikov.courseproject.domain.entities.Student

/* StudentJson -> StudentEntity */
object StudentJsonToStudentEntityMapper : Mapper<StudentJson, StudentEntity>() {

    override fun map(value: StudentJson): StudentEntity {
        return StudentEntity(value.id, value.name, value.grades.last().mark)
    }
}

/* StudentJson -> Student */
object StudentJsonToStudentMapper : Mapper<StudentJson, Student>() {

    override fun map(value: StudentJson): Student {
        return Student(value.id, value.name, value.grades.last().mark, MarkJsonToMarkMapper.map(value.grades.dropLast(1)))
    }
}

/* StudentWithMarks -> Student */
object StudentWithMarksToStudentMapper : Mapper<StudentWithMarks, Student>() {

    override fun map(value: StudentWithMarks): Student {
        return Student(value.student.id, value.student.name, value.student.mark, MarkEntityToMarkMapper.map(value.marks))
    }
}