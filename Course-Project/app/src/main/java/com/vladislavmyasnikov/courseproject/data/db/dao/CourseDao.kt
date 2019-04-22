package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entity.CourseEntity

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses LIMIT 1")
    fun loadFirstCourse(): LiveData<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourse(course: CourseEntity)
}