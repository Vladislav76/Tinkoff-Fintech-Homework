package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.*
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun loadStudents(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudents(students: List<StudentEntity>)

    @Query("DELETE FROM students")
    fun deleteStudents()
}