package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity

@Dao
interface LectureDao {

    @Query("SELECT * FROM lectures ORDER BY id")
    fun loadLectures(): LiveData<List<LectureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLectures(lectures: List<LectureEntity>)
}
