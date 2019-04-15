package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application

import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val mDataRepository: DataRepository
    private val mTasks = MediatorLiveData<List<TaskEntity>>()

    val tasks: LiveData<List<TaskEntity>>
        get() = mTasks

    init {
        mDataRepository = DataRepository.getInstance(application)
    }

    fun init(lectureId: Int) {
        mTasks.addSource(mDataRepository.loadTasks(lectureId)) { tasks -> mTasks.postValue(tasks) }
    }
}
