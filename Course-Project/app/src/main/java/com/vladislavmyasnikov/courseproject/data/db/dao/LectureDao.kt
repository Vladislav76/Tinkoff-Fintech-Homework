package com.vladislavmyasnikov.courseproject.data.db.dao

import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LectureDao {

    @Query("SELECT * FROM lectures")
    fun loadLectures(): LiveData<List<LectureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLectures(lectures: List<LectureEntity>)
}
