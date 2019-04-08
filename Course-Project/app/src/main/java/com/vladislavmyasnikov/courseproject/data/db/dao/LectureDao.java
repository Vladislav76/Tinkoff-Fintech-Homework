package com.vladislavmyasnikov.courseproject.data.db.dao;

import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LectureDao {

    @Query("SELECT * FROM lectures")
    LiveData<List<LectureEntity>> loadLectures();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLectures(List<LectureEntity> lectures);
}
