package com.vladislavmyasnikov.courseproject.data.repositories

import android.app.Application
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors

class LectureRepository private constructor(application: Application) {

    private val localDataSource = LocalDatabase.getInstance(application)
    private val remoteDataSource = NetworkService.getInstance()
    private val memory = Memory.getInstance(application)
    private val executor = Executors.newSingleThreadExecutor()
    private val taskRepository = TaskRepository.getInstance(application)
    private var recentRequestTime: Long = 0
    val lectures = localDataSource.lectureDao().loadLectures()

    fun refreshLectures(callback: LoadLecturesCallback) {
        callback.onResponseReceived(ResponseMessage.LOADING)
        if (isCacheNotDirty()) {
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        } else {
            loadRemoteLectures(callback)
        }
    }

    private fun isCacheNotDirty() = System.currentTimeMillis() - recentRequestTime < CASH_LIFE_TIME_IN_MS

    private fun loadRemoteLectures(callback: LoadLecturesCallback) {
        remoteDataSource.fintechService.getLectures(memory.loadToken()).enqueue(object : Callback<Lectures> {
            override fun onFailure(call: Call<Lectures>, e: Throwable) {
                callback.onResponseReceived(ResponseMessage.NO_INTERNET)
            }

            override fun onResponse(call: Call<Lectures>, response: Response<Lectures>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        recentRequestTime = System.currentTimeMillis()
                        saveLectures(data.lectures, callback)
                    }
                } else {
                    callback.onResponseReceived(ResponseMessage.ERROR)
                }
            }
        })
    }

    private fun saveLectures(lectures: List<Lecture>, callback: LoadLecturesCallback) {
        executor.execute {
            val entities = convertLecturesToEntities(lectures).sortedBy { it.id }
            localDataSource.lectureDao().insertLectures(entities)
            lectures.forEach { taskRepository.saveTasksByLectureId(it.tasks, it.id) }
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        }
    }



    companion object {

        private var INSTANCE: LectureRepository? = null
        private const val CASH_LIFE_TIME_IN_MS = 10_000

        fun getInstance(application: Application): LectureRepository =
                LectureRepository.INSTANCE ?: synchronized(LectureRepository::class.java) {
                    LectureRepository.INSTANCE ?: LectureRepository(application).also { LectureRepository.INSTANCE = it }
                }

        private fun convertLecturesToEntities(lectures: List<Lecture>): List<LectureEntity> {
            val entities = ArrayList<LectureEntity>()
            for (lecture in lectures) {
                entities.add(LectureEntity(lecture.id, lecture.title))
            }
            return entities
        }
    }



    interface LoadLecturesCallback {

        fun onResponseReceived(response: ResponseMessage)
    }
}