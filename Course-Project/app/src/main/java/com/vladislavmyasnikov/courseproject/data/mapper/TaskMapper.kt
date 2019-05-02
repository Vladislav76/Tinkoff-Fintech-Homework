package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.TaskEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.entities.TaskStatus
import com.vladislavmyasnikov.courseproject.domain.entities.TaskType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US).also { it.timeZone = TimeZone.getTimeZone("UTC") }

private fun convertStringToTaskType(value: String): TaskType {
    return when (value) {
        "full" -> TaskType.HOMEWORK
        "test_during_lecture" -> TaskType.TEST
        else -> TaskType.OTHER
    }
}

private fun convertStringToTaskStatus(value: String): TaskStatus {
    return when (value) {
        "new" -> TaskStatus.NEW
        "on_check" -> TaskStatus.ON_CHECK
        "accepted" -> TaskStatus.ACCEPTED
        else -> TaskStatus.OTHER
    }
}

/* ENTITY -> MODEL */
object EntityToModelTaskMapper : Mapper<TaskEntity, Task>() {

    override fun map(value: TaskEntity): Task {
        return Task(value.id, value.title, convertStringToTaskStatus(value.status), value.mark, value.deadline, value.maxScore, convertStringToTaskType(value.taskType))
    }
}

/* JSON -> ENTITY */
object JsonToEntityTaskMapper : Mapper<TaskInfo, TaskEntity>() {

    var lectureId: Int = 0

    override fun map(value: TaskInfo): TaskEntity {
        var date: Date? = null
        try {
            date = format.parse(value.task.deadlineDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        } finally {
            return TaskEntity(value.id, value.task.title, value.status, value.mark, date, value.task.maxScore, lectureId, value.task.taskType)
        }
    }
}