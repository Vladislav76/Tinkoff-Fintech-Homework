package com.vladislavmyasnikov.courseproject.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE lecture_id = :lectureId")
    fun loadTasks(lectureId: Int): LiveData<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<TaskEntity>)
}
