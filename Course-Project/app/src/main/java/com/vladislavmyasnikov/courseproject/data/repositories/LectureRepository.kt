package com.vladislavmyasnikov.courseproject.data.repositories

import android.content.Context
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.FintechService
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.di.components.DaggerDataSourceComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class LectureRepository @Inject constructor(private val applicationContext: Context, private val localDataSource: LocalDatabase, private val remoteDataSource: FintechService, private val memory: Memory) {

    private val executor = Executors.newSingleThreadExecutor()
    private var recentRequestTime: Long = 0
    private val taskRepository: TaskRepository by lazy {
        val component = DaggerDataSourceComponent.builder().contextModule(ContextModule(applicationContext)).build()
        TaskRepository(component.getDatabase())
    }
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
        remoteDataSource.getLectures(memory.loadToken()).enqueue(object : Callback<Lectures> {
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

        private const val CASH_LIFE_TIME_IN_MS = 10_000

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