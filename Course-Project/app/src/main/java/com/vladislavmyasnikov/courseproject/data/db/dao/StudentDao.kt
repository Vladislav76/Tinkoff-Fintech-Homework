package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.*
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentWithMarks

@Dao
interface StudentDao {

    @Transaction
    @Query("SELECT * FROM students")
    fun loadStudentsWithMarks(): List<StudentWithMarks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudents(students: List<StudentEntity>)
}