package com.vladislavmyasnikov.courseproject.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.models.Lecture
import com.vladislavmyasnikov.courseproject.data.models.Student
import com.vladislavmyasnikov.courseproject.data.models.TaskInfo
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.Students
import retrofit2.Callback
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class DataRepository private constructor(application: Application) {

    private val mLocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, DATABASE_NAME).build()
    private val mNetworkService = NetworkService.getInstance()
    private val mExecutor = Executors.newSingleThreadExecutor()

    /*
     * Loading data from database
     */
    fun loadLectures(): LiveData<List<LectureEntity>> {
        return mLocalDatabase.lectureDao().loadLectures()
    }

    fun loadStudents(): LiveData<List<StudentEntity>> {
        return mLocalDatabase.studentDao().loadStudents()
    }

    fun loadTasks(lectureId: Int): LiveData<List<TaskEntity>> {
        return mLocalDatabase.taskDao().loadTasks(lectureId)
    }

    /*
     * Loading data from server
     */
    fun loadLectures(token: String, callback: Callback<Lectures>) {
        mNetworkService.fintechService.getLectures(token).enqueue(callback)
    }

    fun loadStudents(token: String, callback: Callback<List<Students>>) {
        mNetworkService.fintechService.getStudents(token).enqueue(callback)
    }

    /*
     * Inserting data into database
     */
    fun insertLectures(lectures: List<Lecture>) {
        mExecutor.execute { mLocalDatabase.lectureDao().insertLectures(convertLecturesToEntities(lectures)) }
    }

    fun insertTasks(tasks: List<TaskInfo>, lectureId: Int) {
        mExecutor.execute { mLocalDatabase.taskDao().insertTasks(convertTasksToEntities(tasks, lectureId)) }
    }

    fun insertStudents(students: List<Student>) {
        mExecutor.execute { mLocalDatabase.studentDao().insertStudents(convertStudentsToEntities(students)) }
    }



    companion object {

        private const val DATABASE_NAME = "local_database"
        private var sInstance: DataRepository? = null

        fun getInstance(application: Application): DataRepository =
                sInstance ?: synchronized(DataRepository::class.java) { sInstance ?: DataRepository(application).also { sInstance = it }}

        private fun convertStudentsToEntities(students: List<Student>): List<StudentEntity> {
            val entities = ArrayList<StudentEntity>()
            for (student in students) {
                entities.add(StudentEntity(student.id, student.name, student.grades.lastOrNull()!!.mark))
            }
            return entities
        }

        private fun convertLecturesToEntities(lectures: List<Lecture>): List<LectureEntity> {
            val entities = ArrayList<LectureEntity>()
            for (lecture in lectures) {
                entities.add(LectureEntity(lecture.id, lecture.title))
            }
            return entities
        }

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
