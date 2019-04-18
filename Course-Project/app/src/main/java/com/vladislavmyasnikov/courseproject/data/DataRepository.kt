package com.vladislavmyasnikov.courseproject.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bumptech.glide.Glide
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.data.models.*
import com.vladislavmyasnikov.courseproject.data.network.*
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import retrofit2.Callback
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class DataRepository private constructor(application: Application) {

    private val mApplication = application
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
    fun loadLectures(callback: Callback<Lectures>) {
        mNetworkService.fintechService.getLectures(loadToken()).enqueue(callback)
    }

    fun loadStudents(callback: Callback<List<Students>>) {
        mNetworkService.fintechService.getStudents(loadToken()).enqueue(callback)
    }

    fun loadProfile(callback: Callback<ProfileInfo>) {
        mNetworkService.fintechService.getProfile(loadToken()).enqueue(callback)
    }

    fun getAccess(data: Login, callback: Callback<Void>) {
        mNetworkService.fintechService.getAccess(data).enqueue(callback)
    }

    /*
     * Loading data from Shared Preferences
     */
    fun loadCookieData(): CookieData? {
        val preferences = mApplication.getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AUTHORIZATION_TOKEN, null)
        val time = preferences.getString(TOKEN_EXPIRATION_TIME, null)
        return if (token == null || time == null) null else CookieData(token, time)
    }

    fun loadProfile(): Profile? {
        val preferences = mApplication.getSharedPreferences(USER_STORAGE_NAME, Context.MODE_PRIVATE)
        val firstName = preferences.getString(USER_FIRST_NAME, null)
        val lastName = preferences.getString(USER_LAST_NAME, null)
        val middleName = preferences.getString(USER_MIDDLE_NAME, null)
        val avatarUrl = preferences.getString(USER_AVATAR_URL, null) ?: ""
        return if (firstName == null || lastName == null || middleName == null) null else Profile(firstName, lastName, middleName, avatarUrl)
    }

    private fun loadToken(): String {
        val preferences = mApplication.getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, null) ?: ""
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

    /*
     * Saving data into Shared Preferences
     */
    fun saveCookieData(data: CookieData) {
        val preferences = mApplication.getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        preferences.edit()
                .putString(AUTHORIZATION_TOKEN, data.token)
                .putString(TOKEN_EXPIRATION_TIME, data.time)
                .apply()
    }

    fun saveUserData(profile: Profile) {
        val preferences = mApplication.getSharedPreferences(USER_STORAGE_NAME, Context.MODE_PRIVATE)
        preferences.edit()
                .putString(USER_FIRST_NAME, profile.firstName)
                .putString(USER_LAST_NAME, profile.lastName)
                .putString(USER_MIDDLE_NAME, profile.middleName)
                .putString(USER_AVATAR_URL, profile.avatarUrl)
                .apply()
    }



    companion object {

        const val COOKIES_STORAGE_NAME = "cookies_storage"
        const val AUTHORIZATION_TOKEN = "authorization_token"
        const val TOKEN_EXPIRATION_TIME = "token_expiration_time"
        const val USER_STORAGE_NAME = "user_storage"
        const val USER_FIRST_NAME = "user_first_name"
        const val USER_LAST_NAME = "user_last_name"
        const val USER_MIDDLE_NAME = "user_middle_name"
        const val USER_AVATAR_URL = "user_avatar_url"
        const val CASH_LIFE_TIME_IN_MILLISECONDS = 10_000
        const val MINIMAL_PASSWORD_LENGTH = 8

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
