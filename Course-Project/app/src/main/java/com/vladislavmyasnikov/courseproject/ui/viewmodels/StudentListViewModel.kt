package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.network.Students
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val mApplication = application
    private val mDataRepository: DataRepository = DataRepository.getInstance(application)
    private val mMessageString = mApplication.resources.getString(R.string.not_ok_status_message)
    private val mUpdatingDataState = MutableLiveData<String>()

    var recentRequestTime: Long = 0
    val students: LiveData<List<StudentEntity>> = mDataRepository.loadStudents()
    val updatingDataState: LiveData<String>
        get() = mUpdatingDataState

    fun resetUpdatingDataState() {
        mUpdatingDataState.value = null
    }

    fun updateStudents() {
        if (System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME || students.value?.size == 0) {
            loadData()
        } else {
            mUpdatingDataState.value = ""
        }
    }

    private fun loadData() {
        val preferences = mApplication.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null) ?: ""

        mDataRepository.loadStudents(token, object : Callback<List<Students>> {
            override fun onFailure(call: Call<List<Students>>, e: Throwable) {
                mUpdatingDataState.value = mMessageString
                println("onFailure")
            }

            override fun onResponse(call: Call<List<Students>>, response: Response<List<Students>>) {
                val result = response.body()
                if (response.message() == "OK" && result != null && result.isNotEmpty()) {
                    mDataRepository.insertStudents(result[1].students)
                    mUpdatingDataState.value = ""
                    recentRequestTime = System.currentTimeMillis()
                    println("Success!")
                } else {
                    mUpdatingDataState.value = mMessageString
                    println("Not ok...")
                }
            }
        })
    }



    companion object {
        private const val CASH_LIFE_TIME = 10_000
    }
}