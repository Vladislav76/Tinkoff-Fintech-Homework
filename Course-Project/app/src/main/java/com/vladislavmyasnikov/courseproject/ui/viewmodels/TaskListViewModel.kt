package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.repositories.TaskRepository
import com.vladislavmyasnikov.courseproject.ui.main.App

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository by lazy {
        App.instance.repositoryComponent.getTaskRepository()
    }
    private val mLectureId = MutableLiveData<Int>()

    val tasks: LiveData<List<TaskEntity>> = Transformations.switchMap(mLectureId) {
        id -> taskRepository.getTasksByLectureId(id)
    }

    fun loadTasksByLectureId(id: Int) {
        mLectureId.value = id
    }
}
