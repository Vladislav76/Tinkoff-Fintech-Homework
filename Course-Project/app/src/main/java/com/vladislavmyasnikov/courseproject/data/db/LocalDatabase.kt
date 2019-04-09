package com.vladislavmyasnikov.courseproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladislavmyasnikov.courseproject.data.db.converter.DateConverter
import com.vladislavmyasnikov.courseproject.data.db.dao.LectureDao
import com.vladislavmyasnikov.courseproject.data.db.dao.StudentDao
import com.vladislavmyasnikov.courseproject.data.db.dao.TaskDao
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity

@Database(entities = [LectureEntity::class, TaskEntity::class, StudentEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun lectureDao(): LectureDao
    abstract fun taskDao(): TaskDao
    abstract fun studentDao(): StudentDao
}
