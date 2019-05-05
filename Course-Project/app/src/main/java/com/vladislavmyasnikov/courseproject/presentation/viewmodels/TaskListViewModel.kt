package com.vladislavmyasnikov.courseproject.presentation.viewmodels

import androidx.lifecycle.*
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import io.reactivex.Observable
import javax.inject.Inject

class TaskListViewModel(private val taskRepository: ITaskRepository) : ViewModel() {

    fun loadTasksByLectureId(id: Int): Observable<List<Task>> {
        return taskRepository.getTasksByLectureId(id)
    }
}



class TaskListViewModelFactory @Inject constructor(private val taskRepository: ITaskRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            TaskListViewModel(taskRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
