package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.*
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.repositories.TaskRepository
import javax.inject.Inject

class TaskListViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val mLectureId = MutableLiveData<Int>()

    val tasks: LiveData<List<TaskEntity>> = Transformations.switchMap(mLectureId) {
        id -> taskRepository.getTasksByLectureId(id)
    }

    fun loadTasksByLectureId(id: Int) {
        mLectureId.value = id
    }
}



class TaskListViewModelFactory @Inject constructor(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            TaskListViewModel(taskRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
