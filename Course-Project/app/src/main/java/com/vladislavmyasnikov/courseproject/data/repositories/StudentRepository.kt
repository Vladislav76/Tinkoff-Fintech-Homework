package com.vladislavmyasnikov.courseproject.data.repositories

import android.app.Application
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.models.Student
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.Students
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.concurrent.Executors

class StudentRepository private constructor(application: Application) {

    private val localDataSource = LocalDatabase.getInstance(application)
    private val remoteDataSource = NetworkService.getInstance()
    private val memory = Memory.getInstance(application)
    private val executor = Executors.newSingleThreadExecutor()
    private var recentRequestTime: Long = 0
    val students = localDataSource.studentDao().loadStudents()

    fun refreshStudents(callback: LoadStudentsCallback) {
        callback.onResponseReceived(ResponseMessage.LOADING)
        if (isCacheNotDirty()) {
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        } else {
            loadRemoteStudents(callback)
        }
    }

    private fun isCacheNotDirty() = System.currentTimeMillis() - recentRequestTime < CASH_LIFE_TIME_IN_MS

    private fun loadRemoteStudents(callback: LoadStudentsCallback) {
        remoteDataSource.fintechService.getStudents(memory.loadToken()).enqueue(object : Callback<List<Students>> {
            override fun onFailure(call: Call<List<Students>>, e: Throwable) {
                callback.onResponseReceived(ResponseMessage.NO_INTERNET)
            }

            override fun onResponse(call: Call<List<Students>>, response: Response<List<Students>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        recentRequestTime = System.currentTimeMillis()
                        saveStudents(data[1].students, callback)
                    }
                } else {
                    callback.onResponseReceived(ResponseMessage.ERROR)
                }
            }
        })
    }

    private fun saveStudents(students: List<Student>, callback: LoadStudentsCallback) {
        executor.execute {
            val entities = convertStudentsToEntities(students)
            localDataSource.studentDao().insertStudents(entities)
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        }
    }



    companion object {

        private var INSTANCE: StudentRepository? = null
        private const val CASH_LIFE_TIME_IN_MS = 10_000

        fun getInstance(application: Application): StudentRepository =
                StudentRepository.INSTANCE ?: synchronized(StudentRepository::class.java) {
                    StudentRepository.INSTANCE ?: StudentRepository(application).also { StudentRepository.INSTANCE = it }
                }

        private fun convertStudentsToEntities(students: List<Student>): List<StudentEntity> {
            val entities = ArrayList<StudentEntity>()
            for (student in students) {
                entities.add(StudentEntity(student.id, student.name, student.grades.lastOrNull()!!.mark))
            }
            return entities
        }
    }



    interface LoadStudentsCallback {

        fun onResponseReceived(response: ResponseMessage)
    }
}