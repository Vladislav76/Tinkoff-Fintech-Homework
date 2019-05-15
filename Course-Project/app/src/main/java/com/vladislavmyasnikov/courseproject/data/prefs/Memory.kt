package com.vladislavmyasnikov.courseproject.data.prefs

import android.content.Context
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import io.reactivex.Observable
import javax.inject.Inject

class Memory @Inject constructor(applicationContext: Context) {

    private val cookiesStorage = applicationContext.getSharedPreferences(COOKIES_STORAGE, Context.MODE_PRIVATE)
    private val profileStorage = applicationContext.getSharedPreferences(PROFILE_STORAGE, Context.MODE_PRIVATE)

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
                val birthday = profileStorage.getString(BIRTHDAY, null) ?: ""
                val email = profileStorage.getString(EMAIL, null) ?: ""
                val firstName = profileStorage.getString(FIRST_NAME, null) ?: ""
                val lastName = profileStorage.getString(LAST_NAME, null) ?: ""
                val middleName = profileStorage.getString(MIDDLE_NAME, null) ?: ""
                val avatarUrl = profileStorage.getString(AVATAR_URL, null) ?: ""
                val phoneMobile = profileStorage.getString(PHONE_MOBILE, null) ?: ""
                val description = profileStorage.getString(DESCRIPTION, null) ?: ""
                val region = profileStorage.getString(REGION, null) ?: ""
                val faculty = profileStorage.getString(FACULTY, null) ?: ""
                val department = profileStorage.getString(DEPARTMENT, null) ?: ""
                e.onNext(ProfileJson(id, birthday, email, firstName, lastName, middleName, phoneMobile, description, region, faculty, department, avatarUrl))
                e.onComplete()
            } else e.onComplete()
        }
    }

//    fun loadCourse(): UserCourse? {
//        val preferences = applicationContext.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
//        val url = cookiesStorage.getString(COURSE_URL, null)
//        return null //???
//    }

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
                .apply()
    }

//    fun saveCourseData(title: String, url: String) {
//        val preferences = applicationContext.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
//        preferences.edit()
//                .putString(COURSE_TITLE, title)
//                .putString(COURSE_URL, url)
//                .apply()
//    }
//
//    fun saveCourseData(count: Int, past: Int, remaining: Int) {
//        val preferences = applicationContext.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
//        preferences.edit()
//                .putInt(LECTURE_COUNT, count)
//                .putInt(PAST_LECTURE_COUNT, past)
//                .putInt(REMAINING_LECTURE_COUNT, remaining)
//                .apply()
//    }



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

        const val COURSE_STORAGE = "course_storage"
        const val COURSE_TITLE = "course_title"
        const val COURSE_URL = "course_url"
        const val LECTURE_COUNT = "lecture_count"
        const val PAST_LECTURE_COUNT = "past_lecture_count"
        const val REMAINING_LECTURE_COUNT = "remaining_lecture_count"
    }
}