package com.vladislavmyasnikov.courseproject.data.repositories

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.TaskJsonToTaskEntityMapper
import com.vladislavmyasnikov.courseproject.data.mapper.TaskEntityToTaskMapper
import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
        private val localDataSource: LocalDatabase
) : ITaskRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val jsonToEntityMapper = TaskJsonToTaskEntityMapper
    private val entityToTaskMapper = TaskEntityToTaskMapper

    override fun getTasksByLectureId(id: Int): Observable<List<Task>> {
        return localDataSource.taskDao().loadTasks(id)
                .subscribeOn(Schedulers.io())
                .map(entityToTaskMapper::map)
                .toObservable()
    }

    override fun saveTasksByLectureId(tasks: List<TaskInfo>, lectureId: Int) {
        executor.execute {
            jsonToEntityMapper.lectureId = lectureId
            localDataSource.taskDao().insertTasks(jsonToEntityMapper.map(tasks))
        }
    }
}