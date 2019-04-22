package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.repositories.TaskRepository

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository.getInstance(application)
    private val mLectureId = MutableLiveData<Int>()

    val tasks: LiveData<List<TaskEntity>> = Transformations.switchMap(mLectureId) {
        id -> taskRepository.getTasksByLectureId(id)
    }

    fun loadTasksByLectureId(id: Int) {
        mLectureId.value = id
    }
}
