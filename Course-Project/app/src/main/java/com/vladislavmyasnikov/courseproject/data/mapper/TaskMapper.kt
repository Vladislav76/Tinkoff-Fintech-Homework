package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.TaskEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TaskEntityToTaskMapper : Mapper<TaskEntity, Task>() {

    override fun map(value: TaskEntity): Task {
        return Task(value.id, value.title, value.status, value.mark, value.deadline, value.maxScore)
    }
}



object TaskJsonToTaskEntityMapper : Mapper<TaskInfo, TaskEntity>() {

    var lectureId: Int = 0

    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            .also { it.timeZone = TimeZone.getTimeZone("UTC") }

    override fun map(value: TaskInfo): TaskEntity {
        var date: Date? = null
        try {
            date = format.parse(value.task?.deadlineDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        } finally {
            return TaskEntity(value.id, value.task!!.title, value.status, value.mark, date, value.task.maxScore, lectureId)
        }
    }
}