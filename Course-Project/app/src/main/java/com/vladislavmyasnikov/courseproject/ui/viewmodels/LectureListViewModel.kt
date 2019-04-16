package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LectureListViewModel(application: Application) : AndroidViewModel(application) {

    private val mApplication = application
    private val mDataRepository = DataRepository.getInstance(application)
    private val mMessageString = mApplication.resources.getString(R.string.not_ok_status_message)
    private val mUpdatingDataState = MutableLiveData<String>()

    val lectures: LiveData<List<LectureEntity>> = mDataRepository.loadLectures()
    val updatingDataState: LiveData<String>
        get() = mUpdatingDataState

    fun resetUpdatingDataState() {
        mUpdatingDataState.value = null
    }

    fun updateLectures() {
        val preferences = mApplication.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null) ?: ""

        mDataRepository.loadLectures(token, object : Callback<Lectures> {
            override fun onFailure(call: Call<Lectures>, e: Throwable) {
                mUpdatingDataState.value = mMessageString
                println("onFailure")
            }

            override fun onResponse(call: Call<Lectures>, response: Response<Lectures>) {
                val result = response.body()
                if (response.message() == "OK" && result != null) {
                    mDataRepository.insertLectures(result.lectures)
                    for (lecture in result.lectures) {
                        mDataRepository.insertTasks(lecture.tasks, lecture.id)
                    }
                    mUpdatingDataState.value = ""
                    println("success!")

                } else {
                    println("not ok")
                    mUpdatingDataState.value = mMessageString
                }
            }
        })
    }
}
