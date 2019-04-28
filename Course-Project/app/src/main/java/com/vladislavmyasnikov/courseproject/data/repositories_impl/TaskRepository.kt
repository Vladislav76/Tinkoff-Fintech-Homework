package com.vladislavmyasnikov.courseproject.data.repositories_impl

import android.util.Log
import androidx.lifecycle.LiveData
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entities.TaskEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.TaskInfo
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class TaskRepository @Inject constructor(
        private val localDataSource: LocalDatabase
) : ITaskRepository {

    private val executor = Executors.newSingleThreadExecutor()

    fun getTasksByLectureId(lectureId: Int): LiveData<List<TaskEntity>> {
        return localDataSource.taskDao().loadTasks(lectureId)
    }

    fun saveTasksByLectureId(tasks: List<TaskInfo>, lectureId: Int) {
        executor.execute { localDataSource.taskDao().insertTasks(convertTasksToEntities(tasks, lectureId)) }
    }



    companion object {

        private fun convert(entity: TaskEntity): Task =
                Task(entity.id, entity.title, entity.status, entity.mark, entity.deadline, entity.maxScore)

        private fun convertTasksToEntities(tasks: List<TaskInfo>, lectureId: Int): List<TaskEntity> {
            val entities = ArrayList<TaskEntity>()
            for (taskInfo in tasks) {
                val task = taskInfo.task
                var date: Date? = null
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
                    format.timeZone = TimeZone.getTimeZone("UTC")
                    date = if (task?.deadlineDate == null) null else format.parse(task.deadlineDate)
                } catch (e: ParseException) {
                    Log.d("LECTURE_LIST_VIEW_MODEL", "Incorrect string format for converting to the Date class")
                } finally {
                    entities.add(TaskEntity(taskInfo.id, task!!.title, taskInfo.status, taskInfo.mark, date, task.maxScore, lectureId))
                }
            }
            return entities
        }
    }
}