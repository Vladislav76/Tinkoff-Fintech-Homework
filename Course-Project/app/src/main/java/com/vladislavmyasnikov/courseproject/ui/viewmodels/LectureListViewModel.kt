package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.util.Log

import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.Task
import com.vladislavmyasnikov.courseproject.data.models.TaskInfo

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class LectureListViewModel(application: Application) : AndroidViewModel(application) {

    private val mDataRepository: DataRepository
    private val mLectures = MediatorLiveData<List<LectureEntity>>()

    val lectures: LiveData<List<LectureEntity>>
        get() = mLectures

    init {

        mDataRepository = DataRepository.getInstance(application)
        mLectures.addSource(mDataRepository.loadLectures()) { lectureEntities -> mLectures.postValue(lectureEntities) }
    }

    fun updateLectures(lectures: List<Lecture>) {
        mDataRepository.insertLectures(convertLecturesToEntities(lectures))
        for (lecture in lectures) {
            mDataRepository.insertTasks(convertTasksToEntities(lecture.tasks, lecture.id))
        }
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
                date = if (task.deadlineDate == null) null else format.parse(task.deadlineDate)
            } catch (e: ParseException) {
                Log.d("LECTURE_LIST_VIEW_MODEL", "Incorrect string format for converting to the Date class")
            } finally {
                entities.add(TaskEntity(taskInfo.id, task.title, taskInfo.status, taskInfo.mark, date, task.maxScore, lectureId))
            }
        }
        return entities
    }
}
