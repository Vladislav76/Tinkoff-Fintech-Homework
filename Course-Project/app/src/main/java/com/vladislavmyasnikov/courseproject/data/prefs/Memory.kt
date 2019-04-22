package com.vladislavmyasnikov.courseproject.data.prefs

import android.app.Application
import android.content.Context
import com.vladislavmyasnikov.courseproject.data.models.Profile
import com.vladislavmyasnikov.courseproject.data.models.UserCourse
import com.vladislavmyasnikov.courseproject.data.network.CookieData

class Memory private constructor(application: Application) {

    private val mApplication = application

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

    fun loadCourse(): UserCourse? {
        val preferences = mApplication.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
        val url = preferences.getString(COURSE_URL, null)
        return null //???
    }

    fun loadToken(): String {
        val preferences = mApplication.getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, null) ?: ""
    }

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

    fun saveCourseData(title: String, url: String) {
        val preferences = mApplication.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
        preferences.edit()
                .putString(COURSE_TITLE, title)
                .putString(COURSE_URL, url)
                .apply()
    }

    fun saveCourseData(count: Int, past: Int, remaining: Int) {
        val preferences = mApplication.getSharedPreferences(COURSE_STORAGE_NAME, Context.MODE_PRIVATE)
        preferences.edit()
                .putInt(LECTURE_COUNT, count)
                .putInt(PAST_LECTURE_COUNT, past)
                .putInt(REMAINING_LECTURE_COUNT, remaining)
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

        const val COURSE_STORAGE_NAME = "course_storage"
        const val COURSE_TITLE = "course_title"
        const val COURSE_URL = "course_url"
        const val LECTURE_COUNT = "lecture_count"
        const val PAST_LECTURE_COUNT = "past_lecture_count"
        const val REMAINING_LECTURE_COUNT = "remaining_lecture_count"

        private var INSTANCE: Memory? = null

        fun getInstance(application: Application): Memory =
                INSTANCE ?: synchronized(Memory::class.java) { INSTANCE ?: Memory(application).also { INSTANCE = it }}
    }
}