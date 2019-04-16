package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.TaskInfo
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LectureListViewModel(application: Application) : AndroidViewModel(application) {

    private val mApplication = application
    private val mDataRepository = DataRepository.getInstance(application)
    private val mLectures = MediatorLiveData<List<LectureEntity>>()

    val lectures: LiveData<List<LectureEntity>>
        get() = mLectures

    init {
        mLectures.addSource(mDataRepository.loadLectures()) { lectureEntities -> mLectures.postValue(lectureEntities) }
    }

    fun updateLectures() {
        val preferences = mApplication.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null) ?: ""

        mDataRepository.loadLectures(token, object : Callback<Lectures> {
            override fun onFailure(call: Call<Lectures>, e: Throwable) {
            }

            override fun onResponse(call: Call<Lectures>, response: Response<Lectures>) {
                val result = response.body()
                if (response.message() == "OK" && result != null) {
                    Thread {
                        mDataRepository.insertLectures(convertLecturesToEntities(result.lectures))
                        for (lecture in result.lectures) {
                            mDataRepository.insertTasks(convertTasksToEntities(lecture.tasks, lecture.id))
                        }
                    }.start()
                }
            }
        })
    }

    private fun convertLecturesToEntities(lectures: List<Lecture>): List<LectureEntity> {
        val entities = ArrayList<LectureEntity>()
        for (lecture in lectures) {
            entities.add(LectureEntity(lecture.id, lecture.title))
        }
        return entities
    }

    private fun convertTasksToEntities(tasks: List<TaskInfo>, lectureId: Int): List<TaskEntity> {
        val entities = ArrayList<TaskEntity>()
        for (taskInfo in tasks) {
            val task = taskInfo.task
            var date: Date? = null
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
                format.timeZone = TimeZone.getTimeZone("UTC")
                date = if (task?.deadlineDate == null) null else format.parse(task.deadlineDate)
            } catch (e: ParseException) {
                Log.d("LECTURE_LIST_VIEW_MODEL", "Incorrect string format for converting to the Date class")
            } finally {
                entities.add(TaskEntity(taskInfo.id, task!!.title, taskInfo.status, taskInfo.mark, date, task.maxScore, lectureId))
            }
        }
        return entities
    }
}
