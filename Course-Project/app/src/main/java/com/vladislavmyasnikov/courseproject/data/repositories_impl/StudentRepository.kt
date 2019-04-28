package com.vladislavmyasnikov.courseproject.data.repositories_impl

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Students
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class StudentRepository @Inject constructor(
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IStudentRepository {

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
        remoteDataSource.getStudents(memory.loadToken()).enqueue(object : Callback<List<Students>> {
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

    private fun saveStudents(students: List<StudentJson>, callback: LoadStudentsCallback) {
        executor.execute {
            val entities = convertStudentsToEntities(students)
            localDataSource.studentDao().insertStudents(entities)
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        }
    }



    companion object {

        private const val CASH_LIFE_TIME_IN_MS = 10_000

        private fun convertStudentsToEntities(students: List<StudentJson>): List<StudentEntity> {
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