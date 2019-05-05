package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.*
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureWithTasksEntity

@Dao
interface LectureDao {

    @Query("SELECT * FROM lectures")
    fun loadLectures(): List<LectureEntity>

    @Query("SELECT * FROM lectures")
    fun loadLecturesWithTasks(): List<LectureWithTasksEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLectures(lectures: List<LectureEntity>)
}
