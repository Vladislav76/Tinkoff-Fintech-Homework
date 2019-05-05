package com.vladislavmyasnikov.courseproject.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.domain.entities.Course
import io.reactivex.Observable
import javax.inject.Inject

class Memory @Inject constructor(private val applicationContext: Context) {

    private lateinit var cookiesStorage: SharedPreferences
    private lateinit var profileStorage: SharedPreferences
    private lateinit var courseStorage: SharedPreferences

    init {
        init()
    }

    private fun init() {
        cookiesStorage = applicationContext.getSharedPreferences(COOKIES_STORAGE, Context.MODE_PRIVATE)
        profileStorage = applicationContext.getSharedPreferences(PROFILE_STORAGE, Context.MODE_PRIVATE)
        courseStorage = applicationContext.getSharedPreferences(COURSE_STORAGE, Context.MODE_PRIVATE)
    }

    fun clear() {
        cookiesStorage.edit().clear().apply()
        profileStorage.edit().clear().apply()
        courseStorage.edit().clear().apply()
        init()
    }

    /*
     * Loading from storage
     */
    fun loadCookieData(): Observable<CookieData> {
        return Observable.create { e ->
            val token = cookiesStorage.getString(AUTHORIZATION_TOKEN, null)
            val time = cookiesStorage.getString(TOKEN_EXPIRATION_TIME, null)
            if (token != null && time != null) {
                e.onNext(CookieData(token, time))
                e.onComplete()
            } else e.onComplete()
        }
    }

    fun loadToken(): String {
        return cookiesStorage.getString(AUTHORIZATION_TOKEN, null) ?: ""
    }

    fun loadProfile(): Observable<ProfileJson> {
        return Observable.create { e ->
            val id: Int = profileStorage.getInt(USER_ID, -1)
            if (id != -1) {
                val birthday = profileStorage.getString(BIRTHDAY, null)
                val email = profileStorage.getString(EMAIL, null)
                val firstName = profileStorage.getString(FIRST_NAME, null)
                val lastName = profileStorage.getString(LAST_NAME, null)
                val middleName = profileStorage.getString(MIDDLE_NAME, null)
                val avatarUrl = profileStorage.getString(AVATAR_URL, null)
                val phoneMobile = profileStorage.getString(PHONE_MOBILE, null)
                val description = profileStorage.getString(DESCRIPTION, null)
                val region = profileStorage.getString(REGION, null)
                val faculty = profileStorage.getString(FACULTY, null)
                val department = profileStorage.getString(DEPARTMENT, null)
                val university = profileStorage.getString(UNIVERSITY, null)
                e.onNext(ProfileJson(id, birthday, email, firstName, lastName, middleName, phoneMobile, description, region, faculty, department, avatarUrl, university))
                e.onComplete()
            } else e.onComplete()
        }
    }

    fun loadCourseUrlAndTitle(): Observable<Pair<String, String>> {
        return Observable.create { e ->
            val url = courseStorage.getString(COURSE_URL, null)
            val title = courseStorage.getString(COURSE_TITLE, null)
            if (url != null && title != null) {
                e.onNext(url to title)
                e.onComplete()
            } else e.onComplete()
        }
    }

    fun loadCourse(): Observable<Course> {
        return Observable.create { e ->
            val url = courseStorage.getString(COURSE_URL, null)
            val points = courseStorage.getFloat(POINTS, -1.0f)
            if (url != null && points != -1.0f) {
                val title = courseStorage.getString(COURSE_TITLE, null) ?: ""
                val ratingPosition = courseStorage.getInt(RATING_POSITION, 0)
                val studentCount = courseStorage.getInt(STUDENT_COUNT, 0)
                val testCount = courseStorage.getInt(TEST_COUNT, 0)
                val okTestCount = courseStorage.getInt(ACCEPTED_TEST_COUNT, 0)
                val homeworkCount = courseStorage.getInt(HOMEWORK_COUNT, 0)
                val okHomeworkCount = courseStorage.getInt(ACCEPTED_HOMEWORK_COUNT, 0)
                val lectureCount = courseStorage.getInt(LECTURE_COUNT, 0)
                val pastLectureCount = courseStorage.getInt(PAST_LECTURE_COUNT, 0)
                val remainingLectureCount = courseStorage.getInt(REMAINING_LECTURE_COUNT, 0)
                e.onNext(Course(url, title, points, ratingPosition, studentCount, okTestCount, testCount, okHomeworkCount, homeworkCount,
                         pastLectureCount, remainingLectureCount, lectureCount))
                e.onComplete()
            } else e.onComplete()
        }
    }

    fun loadCourseUrl(): String? =
            courseStorage.getString(COURSE_URL, null)

    /*
     * Saving in storage
     */
    fun saveCookieData(data: CookieData) {
        cookiesStorage.edit()
                .putString(AUTHORIZATION_TOKEN, data.token)
                .putString(TOKEN_EXPIRATION_TIME, data.time)
                .apply()
    }

    fun saveProfileData(data: ProfileJson) {
        profileStorage.edit()
                .putInt(USER_ID, data.id)
                .putString(BIRTHDAY, data.birthday)
                .putString(EMAIL, data.email)
                .putString(FIRST_NAME, data.firstName)
                .putString(LAST_NAME, data.lastName)
                .putString(MIDDLE_NAME, data.middleName)
                .putString(AVATAR_URL, data.avatarUrl)
                .putString(PHONE_MOBILE, data.phoneMobile)
                .putString(DESCRIPTION, data.description)
                .putString(REGION, data.region)
                .putString(FACULTY, data.faculty)
                .putString(DEPARTMENT, data.department)
                .putString(UNIVERSITY, data.university)
                .apply()
    }

    fun saveCourseUrlAndTitle(url: String, title: String) {
        courseStorage.edit()
                .putString(COURSE_URL, url)
                .putString(COURSE_TITLE, title)
                .apply()
    }

    fun saveCourse(data: Course) {
        courseStorage.edit()
                .putString(COURSE_TITLE, data.title)
                .putString(COURSE_URL, data.url)
                .putFloat(POINTS, data.points)
                .putInt(RATING_POSITION, data.ratingPosition)
                .putInt(STUDENT_COUNT, data.studentCount)
                .putInt(TEST_COUNT, data.testCount)
                .putInt(ACCEPTED_TEST_COUNT, data.acceptedTestCount)
                .putInt(HOMEWORK_COUNT, data.homeworkCount)
                .putInt(ACCEPTED_HOMEWORK_COUNT, data.acceptedHomeworkCount)
                .putInt(LECTURE_COUNT, data.lectureCount)
                .putInt(PAST_LECTURE_COUNT, data.pastLectureCount)
                .putInt(REMAINING_LECTURE_COUNT, data.remainingLectureCount)
                .apply()
    }

    companion object {
        const val COOKIES_STORAGE = "cookies_storage"
        const val AUTHORIZATION_TOKEN = "authorization_token"
        const val TOKEN_EXPIRATION_TIME = "token_expiration_time"

        const val PROFILE_STORAGE = "profile_storage"
        const val USER_ID = "user_id"
        const val BIRTHDAY = "birthday"
        const val EMAIL = "email"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val MIDDLE_NAME = "middle_name"
        const val PHONE_MOBILE = "phone_mobile"
        const val DESCRIPTION = "description"
        const val REGION = "region"
        const val FACULTY = "faculty"
        const val DEPARTMENT = "department"
        const val AVATAR_URL = "avatar_url"
        const val UNIVERSITY = "university"

        const val COURSE_STORAGE = "course_storage"
        const val COURSE_TITLE = "course_title"
        const val COURSE_URL = "course_url"
        const val POINTS = "points"
        const val RATING_POSITION = "rating"
        const val STUDENT_COUNT = "student_count"
        const val TEST_COUNT = "test_count"
        const val ACCEPTED_TEST_COUNT = "accepted_test_count"
        const val HOMEWORK_COUNT = "homework_count"
        const val ACCEPTED_HOMEWORK_COUNT = "accepted_homework_count"
        const val LECTURE_COUNT = "lecture_count"
        const val PAST_LECTURE_COUNT = "past_lecture_count"
        const val REMAINING_LECTURE_COUNT = "remaining_lecture_count"
    }
}