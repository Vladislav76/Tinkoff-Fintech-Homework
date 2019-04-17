package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.network.Students
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository: DataRepository = DataRepository.getInstance(application)
    private val failMessage = application.resources.getString(R.string.not_ok_status_message)
    private val mutableMessageState = MutableLiveData<String>()
    val students: LiveData<List<StudentEntity>> = dataRepository.loadStudents()
    val messageState: LiveData<String> = mutableMessageState
    var recentRequestTime: Long = 0

    fun updateStudents() {
        if (System.currentTimeMillis() - recentRequestTime > DataRepository.CASH_LIFE_TIME_IN_MILLISECONDS || students.value?.size == 0) {
            loadData()
        } else {
            mutableMessageState.value = ""
        }
    }

    fun resetMessageState() {
        mutableMessageState.value = null
    }

    private fun loadData() {
        dataRepository.loadStudents(object : Callback<List<Students>> {
            override fun onFailure(call: Call<List<Students>>, e: Throwable) {
                mutableMessageState.value = failMessage
            }

            override fun onResponse(call: Call<List<Students>>, response: Response<List<Students>>) {
                val result = response.body()
                if (response.message() == "OK" && result != null && result.isNotEmpty()) {
                    dataRepository.insertStudents(result[1].students)
                    mutableMessageState.value = ""
                    recentRequestTime = System.currentTimeMillis()
                } else {
                    mutableMessageState.value = failMessage
                }
            }
        })
    }
}