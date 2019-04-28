package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import io.reactivex.Observable
import io.reactivex.Single

interface ITaskRepository {

    fun getTasksByLectureId(id: Int): Observable<List<Task>>
    fun saveTasksByLectureId(tasks: List<TaskInfo>, lectureId: Int)
}