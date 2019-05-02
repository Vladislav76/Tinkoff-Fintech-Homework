package com.vladislavmyasnikov.courseproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladislavmyasnikov.courseproject.data.db.converter.DateConverter
import com.vladislavmyasnikov.courseproject.data.db.dao.*
import com.vladislavmyasnikov.courseproject.data.db.entities.*

@Database(entities = [LectureEntity::class, TaskEntity::class, StudentEntity::class, CourseEntity::class, MarkEntity::class, EventEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun lectureDao(): LectureDao
    abstract fun taskDao(): TaskDao
    abstract fun studentDao(): StudentDao
    abstract fun courseDao(): CourseDao
    abstract fun markDao(): MarkDao
    abstract fun eventDao(): EventDao
}
