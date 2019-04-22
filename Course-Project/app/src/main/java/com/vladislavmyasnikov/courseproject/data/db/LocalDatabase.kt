package com.vladislavmyasnikov.courseproject.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladislavmyasnikov.courseproject.data.db.converter.DateConverter
import com.vladislavmyasnikov.courseproject.data.db.dao.CourseDao
import com.vladislavmyasnikov.courseproject.data.db.dao.LectureDao
import com.vladislavmyasnikov.courseproject.data.db.dao.StudentDao
import com.vladislavmyasnikov.courseproject.data.db.dao.TaskDao
import com.vladislavmyasnikov.courseproject.data.db.entity.CourseEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity

@Database(entities = [LectureEntity::class, TaskEntity::class, StudentEntity::class, CourseEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun lectureDao(): LectureDao
    abstract fun taskDao(): TaskDao
    abstract fun studentDao(): StudentDao
    abstract fun courseDao(): CourseDao



    companion object {

        private const val DATABASE_NAME = "local_database"
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase =
                INSTANCE ?: synchronized(LocalDatabase::class.java) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context): LocalDatabase = Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME).build()
    }
}
