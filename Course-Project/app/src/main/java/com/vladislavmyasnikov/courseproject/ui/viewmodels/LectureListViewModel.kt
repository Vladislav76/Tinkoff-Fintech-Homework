package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

class LectureListViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository = DataRepository.getInstance(application)
    private val failMessage = application.resources.getString(R.string.not_ok_status_message)
    private val mutableMessageState = MutableLiveData<String>()
    private val isRequestAllowed = AtomicBoolean(true)
    val lectures: LiveData<List<LectureEntity>> = dataRepository.loadLectures()
    val messageState: LiveData<String> = mutableMessageState
    var recentRequestTime: Long = 0

    fun updateLectures() {
        if (System.currentTimeMillis() - recentRequestTime > DataRepository.CASH_LIFE_TIME_IN_MILLISECONDS) {
            loadData()
        } else {
            mutableMessageState.value = ""
        }
    }

    fun resetMessageState() {
        mutableMessageState.value = null
    }

    private fun loadData() {
        if (isRequestAllowed.compareAndSet(true, false)) {
            dataRepository.loadLectures(object : Callback<Lectures> {
                override fun onFailure(call: Call<Lectures>, e: Throwable) {
                    mutableMessageState.value = failMessage
                    isRequestAllowed.set(true)
                }

                override fun onResponse(call: Call<Lectures>, response: Response<Lectures>) {
                    val result = response.body()
                    if (response.message() == "OK" && result != null) {
                        dataRepository.insertLectures(result.lectures)
                        for (lecture in result.lectures) {
                            dataRepository.insertTasks(lecture.tasks, lecture.id)
                        }
                        mutableMessageState.value = ""
                        recentRequestTime = System.currentTimeMillis()
                    } else {
                        mutableMessageState.value = failMessage
                    }
                    isRequestAllowed.set(true)
                }
            })
        }
    }
}
