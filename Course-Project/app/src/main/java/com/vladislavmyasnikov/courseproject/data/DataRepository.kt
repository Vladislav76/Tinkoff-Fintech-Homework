package com.vladislavmyasnikov.courseproject.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.Students
import retrofit2.Callback
import java.util.concurrent.Executors

class DataRepository private constructor(application: Application) {

    private val mLocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, DATABASE_NAME).build()
    private val mNetworkService = NetworkService.getInstance()
    private val mExecutor = Executors.newSingleThreadExecutor()

    /*
     * Loading data from database
     */
    fun loadLectures(): LiveData<List<LectureEntity>> {
        return mLocalDatabase.lectureDao().loadLectures()
    }

    fun loadStudents(): LiveData<List<StudentEntity>> {
        return mLocalDatabase.studentDao().loadStudents()
    }

    fun loadTasks(lectureId: Int): LiveData<List<TaskEntity>> {
        return mLocalDatabase.taskDao().loadTasks(lectureId)
    }

    /*
     * Loading data from server
     */
    fun loadLectures(token: String, callback: Callback<Lectures>) {
        mNetworkService.fintechService.getLectures(token).enqueue(callback)
    }

    fun loadStudents(token: String, callback: Callback<List<Students>>) {
        mNetworkService.fintechService.getStudents(token).enqueue(callback)
    }

    /*
     * Inserting data into database
     */
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

        private const val DATABASE_NAME = "local_database"
        private var sInstance: DataRepository? = null

        fun getInstance(application: Application): DataRepository =
                sInstance ?: synchronized(DataRepository::class.java) { sInstance ?: DataRepository(application).also { sInstance = it }}
    }
}
