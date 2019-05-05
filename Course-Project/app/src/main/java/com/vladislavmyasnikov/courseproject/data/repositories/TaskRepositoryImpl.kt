package com.vladislavmyasnikov.courseproject.data.repositories

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.EntityToModelTaskMapper
import com.vladislavmyasnikov.courseproject.data.mapper.JsonToEntityTaskMapper
import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
        private val localDataSource: LocalDatabase
) : ITaskRepository {

    override fun getTasksByLectureId(id: Int): Observable<List<Task>> =
            Observable.fromCallable { localDataSource.taskDao().loadTasks(id) }
                    .map(EntityToModelTaskMapper::map)

    override fun saveTasksByLectureId(tasks: List<TaskInfo>, lectureId: Int) {
        JsonToEntityTaskMapper.lectureId = lectureId
        localDataSource.taskDao().insertTasks(JsonToEntityTaskMapper.map(tasks))
    }
}