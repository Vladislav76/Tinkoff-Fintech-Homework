package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entities.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun loadEvents(): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(events: List<EventEntity>)
}