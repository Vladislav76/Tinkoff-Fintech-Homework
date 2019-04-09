package com.vladislavmyasnikov.courseproject.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataRepository private constructor(application: Application) {

    private val mLocalDatabase: LocalDatabase
    private val mExecutor: Executor

    init {
        mLocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, DATABASE_NAME).build()
        mExecutor = Executors.newSingleThreadExecutor()
    }

    fun loadLectures(): LiveData<List<LectureEntity>> {
        return mLocalDatabase.lectureDao().loadLectures()
    }

    fun loadStudents(): LiveData<List<StudentEntity>> {
        return mLocalDatabase.studentDao().loadStudents()
    }

    fun loadTasks(lectureId: Int): LiveData<List<TaskEntity>> {
        return mLocalDatabase.taskDao().loadTasks(lectureId)
    }

    fun insertLectures(lectures: List<LectureEntity>) {
        mExecutor.execute { mLocalDatabase.lectureDao().insertLectures(lectures) }
    }

    fun insertTasks(tasks: List<TaskEntity>) {
        mExecutor.execute { mLocalDatabase.taskDao().insertTasks(tasks) }
    }

    fun insertStudents(students: List<StudentEntity>) {
        mExecutor.execute { mLocalDatabase.studentDao().insertStudents(students) }
    }



    companion object {

        private val DATABASE_NAME = "local_database"
        private var sInstance: DataRepository? = null

        fun getInstance(application: Application): DataRepository =
                sInstance ?: synchronized(DataRepository::class.java) { sInstance ?: DataRepository(application).also { sInstance = it }}
    }
}
