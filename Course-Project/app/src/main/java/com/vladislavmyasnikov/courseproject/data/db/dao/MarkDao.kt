package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entities.MarkEntity

@Dao
interface MarkDao {

    @Query("SELECT * FROM marks WHERE student_id = :id")
    fun loadMarks(id: Int): List<MarkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarks(tasks: List<MarkEntity>)
}