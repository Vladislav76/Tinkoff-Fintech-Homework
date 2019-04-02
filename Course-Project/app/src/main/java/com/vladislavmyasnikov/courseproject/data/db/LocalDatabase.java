package com.vladislavmyasnikov.courseproject.data.db;

import com.vladislavmyasnikov.courseproject.data.db.converter.DateConverter;
import com.vladislavmyasnikov.courseproject.data.db.dao.LectureDao;
import com.vladislavmyasnikov.courseproject.data.db.dao.TaskDao;
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {LectureEntity.class, TaskEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract LectureDao lectureDao();
    public abstract TaskDao taskDao();
}
