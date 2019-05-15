package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun loadStudents(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudents(students: List<StudentEntity>)
}