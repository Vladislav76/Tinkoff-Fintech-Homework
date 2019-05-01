package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entities.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE lecture_id = :id")
    fun loadTasks(id: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<TaskEntity>)
}
