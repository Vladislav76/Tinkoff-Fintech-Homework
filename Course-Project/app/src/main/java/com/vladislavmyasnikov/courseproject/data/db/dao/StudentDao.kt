package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import io.reactivex.Single

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun loadStudents(): Single<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudents(students: List<StudentEntity>)
}