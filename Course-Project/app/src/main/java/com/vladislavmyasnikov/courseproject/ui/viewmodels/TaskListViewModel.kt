package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val mDataRepository = DataRepository.getInstance(application)
    private val mTasks = MediatorLiveData<List<TaskEntity>>()

    val tasks: LiveData<List<TaskEntity>>
        get() = mTasks

    fun init(lectureId: Int) {
        mTasks.addSource(mDataRepository.loadTasks(lectureId)) { tasks -> mTasks.postValue(tasks) }
    }
}
