package com.vladislavmyasnikov.courseproject.data.db.dao;

import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks WHERE lecture_id = :lectureId")
    LiveData<List<TaskEntity>> loadTasks(int lectureId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTasks(List<TaskEntity> tasks);
}
